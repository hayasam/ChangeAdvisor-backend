package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.*;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Reader for document clusters.
 * Reads clusters one at a time.
 */
@Component
public class ClusterReader implements ItemReader<Cluster> {

    private final TopicRepository topicRepository;

    private final TopicAssignmentRepository topicAssignmentRepository;

    private Iterator<Topic> topicIterator;

    @Autowired
    public ClusterReader(TopicRepository topicRepository, TopicAssignmentRepository topicAssignmentRepository) {
        this.topicAssignmentRepository = topicAssignmentRepository;
        this.topicRepository = topicRepository;
    }

    @Override
    public Cluster read() throws Exception {
        if (topicIterator == null) {
            topicIterator = getIterator();
        }
        return getNextCluster().orElse(null);
    }

    private Iterator<Topic> getIterator() {
        List<Topic> topics = topicRepository.findAll();
        return topics.iterator();
    }

    private Optional<Cluster> getNextCluster() {
        if (topicIterator.hasNext()) {
            final int topicId = topicIterator.next().getTopic();
            List<TopicAssignment> assignmentsByTopic = topicAssignmentRepository.findByTopic(topicId);
            return Optional.of(new Cluster(assignmentsByTopic));
        }
        return Optional.empty();
    }
}
