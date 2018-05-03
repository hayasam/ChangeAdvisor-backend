package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Reader for document clusters.
 * Reads all clusters in one go.
 */
@Component
public class BulkClusterReader implements ItemReader<TopicClusteringResult> {

    private static final Logger logger = LoggerFactory.getLogger(BulkClusterReader.class);

    private final TopicRepository topicRepository;

    private final TopicAssignmentRepository topicAssignmentRepository;

    private boolean hasRead = false;

    @Autowired
    public BulkClusterReader(TopicRepository topicRepository, TopicAssignmentRepository topicAssignmentRepository) {
        this.topicRepository = topicRepository;
        this.topicAssignmentRepository = topicAssignmentRepository;
    }

    @Override
    public TopicClusteringResult read() throws Exception {
        if (hasRead) {
            return null;
        }
        TopicClusteringResult topicClusteringResult = readFromRepository();
        hasRead = true;
        logger.info(String.format("Read from repositories %d topics and %d assignments",
                topicClusteringResult.topicSize(), topicClusteringResult.assignmentSize()));
        return topicClusteringResult;
    }

    private TopicClusteringResult readFromRepository() {
        List<Topic> topics = topicRepository.findAll();
        List<TopicAssignment> assignments = topicAssignmentRepository.findAll();
        return new TopicClusteringResult(topics, assignments);
    }
}
