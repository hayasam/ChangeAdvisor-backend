package ch.uzh.ifi.seal.changeadvisor.source;

import ch.uzh.ifi.seal.changeadvisor.batch.job.SourceComponentsTransformationStepConfig;
import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.service.ProjectService;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SourceImportJobFactory {

    private static final String SOURCE_IMPORT = "sourceImport";

    private static final String STEP_NAME = "sourceImportStep";

    private final StepBuilderFactory stepBuilderFactory;

    private final JobBuilderFactory jobBuilderFactory;

    private final SourceComponentsTransformationStepConfig sourceComponentsTransformationStepConfig;

    private final ProjectService projectService;

    @Autowired
    public SourceImportJobFactory(StepBuilderFactory stepBuilderFactory, JobBuilderFactory jobBuilderFactory, SourceComponentsTransformationStepConfig sourceComponentsTransformationStepConfig, ProjectService projectService) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.sourceComponentsTransformationStepConfig = sourceComponentsTransformationStepConfig;
        this.projectService = projectService;
    }

    public Job importAndProcessingJob(SourceCodeDirectoryDto dto) {
        return jobBuilderFactory.get(SOURCE_IMPORT)
                .incrementer(new RunIdIncrementer())
                .flow(sourceImport(dto))
                .next(sourceProcessing())
                .end()
                .build();
    }

    public Job processingJob(final String appName) {
        return jobBuilderFactory.get(SOURCE_IMPORT)
                .incrementer(new RunIdIncrementer())
                .flow(sourceProcessing(appName))
                .end()
                .build();
    }

    private Step sourceImport(SourceCodeDirectoryDto dto) {
        SourceImportTasklet importTasklet = new SourceImportTasklet(dto, projectService);
        return stepBuilderFactory.get(STEP_NAME)
                .allowStartIfComplete(true)
                .tasklet(importTasklet)
                .listener(executionContextPromotionListener())
                .build();
    }

    private Step sourceProcessing(final String appName) {
        Project project = projectService.findByAppName(appName);
        return sourceComponentsTransformationStepConfig.extractBagOfWords(project.getPath());
    }

    private Step sourceProcessing() {
        return sourceComponentsTransformationStepConfig.extractBagOfWordsDeferredPath();
    }

    private ExecutionContextPromotionListener executionContextPromotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"directory"});
        return listener;
    }
}
