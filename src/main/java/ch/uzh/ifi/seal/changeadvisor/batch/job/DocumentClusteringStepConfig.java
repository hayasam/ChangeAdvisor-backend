package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.*;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import com.google.common.collect.Sets;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by alex on 24.07.2017.
 */
@Component
public class DocumentClusteringStepConfig {

    private static final String STEP_NAME = "documents_clustering";

    private final StepBuilderFactory stepBuilderFactory;

    private final TopicWriter topicWriter;

    private final TransformedFeedbackReader mongoFeedbackReader;

    private final TransformedFeedbackRepository feedbackRepository;

    private static final int DEFAUL_MAX_ITERATIONS = 100;

    private int maxIterations = -1;

    @Autowired
    public DocumentClusteringStepConfig(StepBuilderFactory stepBuilderFactory, TopicWriter topicWriter, TransformedFeedbackReader mongoFeedbackReader, TransformedFeedbackRepository feedbackRepository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.topicWriter = topicWriter;
        this.mongoFeedbackReader = mongoFeedbackReader;
        this.feedbackRepository = feedbackRepository;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    @Bean
    public Step documentsClustering() {
        return stepBuilderFactory.get(STEP_NAME)
                .<List<TransformedFeedback>, TopicClusteringResult>chunk(10)
                .reader(mongoFeedbackReader)
                .processor(topicClustering())
                .writer(topicWriter)
                .build();
    }

    public Step documentsClustering(final String appName) {
        return stepBuilderFactory.get(STEP_NAME)
                .<List<TransformedFeedback>, TopicClusteringResult>chunk(1)
                .reader(reader(appName))
                .processor(topicClustering())
                .writer(topicWriter)
                .build();
    }

    private ItemReader<List<TransformedFeedback>> reader(final String appName) {
        return new TransformedFeedbackReader(feedbackRepository, appName);
    }

    public TopicClustering topicClustering() {
        return new TopicClustering(maxIterations < 1 ? DEFAUL_MAX_ITERATIONS : maxIterations);
    }

    @Bean
    public FlatFileTransformedFeedbackReader flatFileTransformedFeedbackReader() {
        final String filePath = "test_files_parser/transformed_feedback/feedback.csv";
        final Set<String> inputCategories = Sets.newHashSet("FEATURE REQUEST", "PROBLEM DISCOVERY");
        return new FlatFileTransformedFeedbackReader(filePath, inputCategories);
    }
}
