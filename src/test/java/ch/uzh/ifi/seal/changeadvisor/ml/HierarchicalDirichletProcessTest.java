package ch.uzh.ifi.seal.changeadvisor.ml;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.FlatFileTransformedFeedbackReader;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Topic;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HierarchicalDirichletProcessTest {

    private static final String FILE_PATH = "test_files_parser/transformed_feedback/feedback.csv";

    private static final Logger logger = LoggerFactory.getLogger(HierarchicalDirichletProcessTest.class);

    private static Set<String> inputCategories = Sets.newHashSet("FEATURE REQUEST", "PROBLEM DISCOVERY");

    @Test
    public void fit() throws Exception {
        HierarchicalDirichletProcess hdplda = new HierarchicalDirichletProcess(1.0, 0.5, 1.0);
        FlatFileTransformedFeedbackReader reader = new FlatFileTransformedFeedbackReader(FILE_PATH, inputCategories);
        List<TransformedFeedback> read = reader.read();
        List<List<String>> documents = read.stream().map(f -> new ArrayList<>(f.getBagOfWords())).collect(Collectors.toList());
        List<String> originalSentences = read.stream().map(TransformedFeedback::getSentence).collect(Collectors.toList());
        Corpus corpus = new Corpus(originalSentences, documents);
        hdplda.fit(corpus, 50);
        List<Topic> topics = hdplda.topics();
        List<TopicAssignment> assignments = hdplda.assignments();

        logger.info(String.format("Corpus size: %d", corpus.size()));
        logger.info(String.format("Topics: %d", topics.size()));
        logger.info(String.format("Topic Assignments: %d", assignments.size()));
    }

}