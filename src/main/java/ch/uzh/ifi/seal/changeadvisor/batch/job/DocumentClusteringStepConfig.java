package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicClustering;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicClusteringResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicWriter;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TransformedFeedbackReader;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by alex on 24.07.2017.
 */
@Component
public class DocumentClusteringStepConfig {

    private static final String STEP_NAME = "documents_clustering";

    private final StepBuilderFactory stepBuilderFactory;

    private final TopicWriter topicWriter;

    private final TransformedFeedbackRepository feedbackRepository;

    private static final int DEFAUL_MAX_ITERATIONS = 100;

    private int maxIterations = -1;

    @Autowired
    public DocumentClusteringStepConfig(StepBuilderFactory stepBuilderFactory, TopicWriter topicWriter, TransformedFeedbackRepository feedbackRepository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.topicWriter = topicWriter;
        this.feedbackRepository = feedbackRepository;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public Step documentsClustering(final String appName) {
        return stepBuilderFactory.get(STEP_NAME)
                .<List<TransformedFeedback>, TopicClusteringResult>chunk(1)
                .reader(reader(appName))
                .processor(topicClustering())
                .writer(topicWriter)
                .build();
    }

    private TransformedFeedbackReader reader(final String appName) {
        return new TransformedFeedbackReader(feedbackRepository, appName);
    }

    public TopicClustering topicClustering() {
        return new TopicClustering(maxIterations < 1 ? DEFAUL_MAX_ITERATIONS : maxIterations);
    }
}
