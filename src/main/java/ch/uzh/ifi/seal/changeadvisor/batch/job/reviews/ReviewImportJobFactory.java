package ch.uzh.ifi.seal.changeadvisor.batch.job.reviews;

import ch.uzh.ifi.seal.changeadvisor.batch.job.*;
import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.project.ProjectRepository;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewAnalysisDto;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Component
public class ReviewImportJobFactory {

    private static final String REVIEW_IMPORT = "reviewImport";

    private static final String REVIEW_ANALYSIS = "reviewAnalysis";

    private static final String STEP_NAME = "reviewImportStep";

    private static final RunIdIncrementer RUN_ID_INCREMENTER = new RunIdIncrementer();

    private final StepBuilderFactory stepBuilderFactory;

    private final JobBuilderFactory jobBuilderFactory;

    private final ArdocStepConfig ardocConfig;

    private final FeedbackTransformationStepConfig feedbackTransformationStepConfig;

    private final DocumentClusteringStepConfig documentClusteringStepConfig;

    private final TermFrequencyInverseDocumentFrequencyStepConfig tfidfStepConfig;

    private final LinkingStepConfig linkingStepConfig;

    private final ProjectRepository projectRepository;

    @Autowired
    public ReviewImportJobFactory(StepBuilderFactory stepBuilderFactory, JobBuilderFactory reviewImportJobBuilder,
                                  ArdocStepConfig ardocConfig,
                                  FeedbackTransformationStepConfig feedbackTransformationStepConfig,
                                  DocumentClusteringStepConfig documentClusteringStepConfig,
                                  TermFrequencyInverseDocumentFrequencyStepConfig tfidfStepConfig,
                                  LinkingStepConfig linkingStepConfig, ProjectRepository projectRepository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = reviewImportJobBuilder;
        this.ardocConfig = ardocConfig;
        this.feedbackTransformationStepConfig = feedbackTransformationStepConfig;
        this.documentClusteringStepConfig = documentClusteringStepConfig;
        this.tfidfStepConfig = tfidfStepConfig;
        this.linkingStepConfig = linkingStepConfig;
        this.projectRepository = projectRepository;
    }

    synchronized public Job job(Map<String, Object> params) {
        Project project = getApp(params).orElseThrow(() ->
                new IllegalArgumentException(String.format("No project found for app name [%s].", params.get("apps"))));
        final String googlePlayId = project.getGooglePlayId();
        return jobBuilderFactory.get(REVIEW_IMPORT)
                .incrementer(RUN_ID_INCREMENTER)
                .flow(reviewImport(Lists.newArrayList(googlePlayId), params))
                .next(ardocConfig.ardocAnalysis(googlePlayId))
                .next(feedbackTransformationStepConfig.transformFeedback(googlePlayId))
                .next(documentClusteringStepConfig.documentsClustering(googlePlayId))
                .next(tfidfStepConfig.computeLabels(googlePlayId))
                .next(linkingStepConfig.clusterLinking(googlePlayId))
                .end()
                .build();
    }

    @SuppressWarnings("unchecked")
    private Optional<Project> getApp(Map<String, Object> params) {
        String apps = (String) params.get("apps");
        String id = (String) params.get("id");
        if (!StringUtils.isEmpty(apps)) {
            return Optional.ofNullable(projectRepository.findByAppName(apps));
        }
        return projectRepository.findById(id);
    }

    private Step reviewImport(ArrayList<String> apps, Map<String, Object> params) {
        ReviewsConfigurationManager configManager = ReviewsConfigurationManager.from(params);
        return stepBuilderFactory.get(STEP_NAME)
                .allowStartIfComplete(true)
                .tasklet(new ReviewImportTasklet(apps, configManager.getConfig()))
                .build();
    }

    public Job reviewAnalysis(ReviewAnalysisDto dto) {
        String app = dto.getApp();
        return jobBuilderFactory.get(REVIEW_ANALYSIS)
                .incrementer(RUN_ID_INCREMENTER)
                .flow(ardocConfig.ardocAnalysis(app))
                .next(feedbackTransformationStepConfig.transformFeedback(app))
                .next(documentClusteringStepConfig.documentsClustering(app))
                .end()
                .build();
    }

    public Job reviewProcessing(ReviewAnalysisDto dto) {
        String app = dto.getApp();
        return jobBuilderFactory.get(REVIEW_ANALYSIS)
                .incrementer(RUN_ID_INCREMENTER)
                .flow(feedbackTransformationStepConfig.transformFeedback(app))
                .next(documentClusteringStepConfig.documentsClustering(app))
                .end()
                .build();
    }

    public Job reviewClustering(ReviewAnalysisDto dto) {
        String app = dto.getApp();
        return jobBuilderFactory.get(REVIEW_ANALYSIS)
                .incrementer(RUN_ID_INCREMENTER)
                .flow(documentClusteringStepConfig.documentsClustering(app))
                .end()
                .build();
    }

    public Job labelReviews(ReviewAnalysisDto dto) {
        String app = dto.getApp();
        return jobBuilderFactory.get(REVIEW_ANALYSIS)
                .incrementer(RUN_ID_INCREMENTER)
                .flow(tfidfStepConfig.computeLabels(app))
                .end()
                .build();
    }
}
