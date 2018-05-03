package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.LabelRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.TopLabelTasklet;
import ch.uzh.ifi.seal.changeadvisor.service.ReviewAggregationService;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TermFrequencyInverseDocumentFrequencyStepConfig {

    private final StepBuilderFactory stepBuilderFactory;

    private static final String STEP_NAME = "compute_top_tfidf_labels";

    private final ReviewAggregationService service;

    private final LabelRepository labelRepository;

    @Autowired
    public TermFrequencyInverseDocumentFrequencyStepConfig(StepBuilderFactory stepBuilderFactory, ReviewAggregationService service, LabelRepository labelRepository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.service = service;
        this.labelRepository = labelRepository;
    }

    public Step computeLabels(final String appName) {
        return stepBuilderFactory.get(STEP_NAME)
                .tasklet(labeler(appName))
                .build();
    }

    private TopLabelTasklet labeler(final String appName) {
        return new TopLabelTasklet(service, appName, labelRepository);
    }
}
