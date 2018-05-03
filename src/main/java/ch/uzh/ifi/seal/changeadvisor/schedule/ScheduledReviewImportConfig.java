package ch.uzh.ifi.seal.changeadvisor.schedule;

import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.project.ReviewsConfig;
import ch.uzh.ifi.seal.changeadvisor.service.FailedToRunJobException;
import ch.uzh.ifi.seal.changeadvisor.service.ProjectService;
import ch.uzh.ifi.seal.changeadvisor.service.ReviewImportService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
@EnableScheduling
public class ScheduledReviewImportConfig implements SchedulingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledReviewImportConfig.class);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final ReviewImportService reviewImportService;

    private final ProjectService projectService;

    private ScheduledTaskRegistrar taskRegistrar;

    private Map<String, ScheduledTask> scheduledTasks;

    @Autowired
    public ScheduledReviewImportConfig(ReviewImportService reviewImportService, ProjectService projectService) {
        this.reviewImportService = reviewImportService;
        this.projectService = projectService;
        this.scheduledTasks = new HashMap<>();
    }

    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService taskExecutor() {
        return Executors.newScheduledThreadPool(100);
    }

    @Override
    public void configureTasks(@NotNull ScheduledTaskRegistrar taskRegistrar) {
        if (this.taskRegistrar == null) {
            this.taskRegistrar = taskRegistrar;
        }
        this.taskRegistrar.setScheduler(taskExecutor());
        configureTasks();
    }

    private void configureTasks() {
        logger.info("Configuring next scheduled review imports.");
        Collection<Project> projects = projectService.findAll();

        for (Project project : projects) {
            if (project.hasCronSchedule()) {
                setSchedule(project);
            }
        }
    }

    public void setSchedule(final Project project) {
        logger.info("Updating schedule for [%s]", project.getAppName());

        final String projectId = project.getId();

        TriggerTask task = triggerTask(project);
        cancelScheduledTaskIfAnyExists(projectId);

        ScheduledTask scheduledTask = taskRegistrar.scheduleTriggerTask(task);
        scheduledTasks.put(projectId, scheduledTask);
    }

    private TriggerTask triggerTask(final Project project) {
        Trigger nextExecutionTrigger = trigger(project.getId());
        return new TriggerTask(
                () -> startReviewImport(project),
                nextExecutionTrigger
        );
    }

    private Trigger trigger(final String projectId) {
        return triggerContext -> {
            Project project = projectService.findById(projectId).orElseThrow(IllegalArgumentException::new);

            Date next = getNextExecutionTime(project.getCronSchedule());
            project.setReviewsConfig(ReviewsConfig.of(project.getReviewsConfig(), next));
            projectService.save(project);
            logger.info("Setting next execution time for [%s]: %s", project.getGooglePlayId(), next);
            return next;
        };
    }

    private Date getNextExecutionTime(final String cronExpression) {
        CronSequenceGenerator sequenceGenerator = new CronSequenceGenerator(cronExpression);
        return sequenceGenerator.next(new Date());
    }

    private void startReviewImport(final Project project) {
        try {
            logger.info("The time is now %s. Starting review import for [%s].",
                    dateFormat.format(new Date()),
                    project.getAppName());
            Map<String, Object> params = createReviewImportParams(project);
            reviewImportService.reviewImport(params);

            ReviewsConfig mostRecentRun = new ReviewsConfig(new Date(), project.getReviewsConfig().getNextReviewImport());
            project.setReviewsConfig(mostRecentRun);
            Optional<Project> updatedProject = projectService.findById(project.getId());
            updatedProject.ifPresent(p -> {
                p.setReviewsConfig(mostRecentRun);
                projectService.save(p);
            });

        } catch (FailedToRunJobException e) {
            logger.error(String.format("Failed to start scheduled review import for [%s].", project.getAppName()), e);
        }
    }

    private Map<String, Object> createReviewImportParams(final Project project) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", project.getId());
        params.put("limit", 5000);
        return params;
    }

    private void cancelScheduledTaskIfAnyExists(final String projectId) {
        if (scheduledTasks.containsKey(projectId)) {
            ScheduledTask previouslyScheduledTask = scheduledTasks.get(projectId);
            previouslyScheduledTask.cancel();
        }
    }
}
