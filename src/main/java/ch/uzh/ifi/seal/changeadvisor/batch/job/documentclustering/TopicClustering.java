package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.ml.Corpus;
import ch.uzh.ifi.seal.changeadvisor.ml.DocumentClusterer;
import ch.uzh.ifi.seal.changeadvisor.ml.DocumentClustererAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

/**
 * Created by alex on 24.07.2017.
 */
public class TopicClustering implements ItemProcessor<List<TransformedFeedback>, TopicClusteringResult> {

    private static final Logger logger = LoggerFactory.getLogger(TopicClustering.class);

    private DocumentClusterer documentClusterer;

    private final int maxIterations;

    public TopicClustering(int maxIterations) {
        this.maxIterations = maxIterations;
        documentClusterer = new DocumentClustererAdapter();
    }

    @Override
    public TopicClusteringResult process(List<TransformedFeedback> items) {
        Corpus corpus = Corpus.of(items);

        logger.info(String.format("Starting clustering of (%d) documents", corpus.size()));
        long start = System.currentTimeMillis();

        documentClusterer.fit(corpus, maxIterations);

        long end = System.currentTimeMillis();
        logger.info(String.format("Finished clustering, time elapsed: %.2f", (end - start) / 1000.));

        List<TopicAssignment> assignments = documentClusterer.assignments();
        List<Topic> topics = documentClusterer.topics();

        logger.info(String.format("Topics: %d", topics.size()));
        logger.info(String.format("Topic Assignments: %d", assignments.size()));
        return new TopicClusteringResult(topics, assignments);
    }
}
