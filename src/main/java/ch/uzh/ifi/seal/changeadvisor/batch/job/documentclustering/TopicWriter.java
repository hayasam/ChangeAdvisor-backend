package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Created by alex on 24.07.2017.
 */
@Component
public class TopicWriter implements ItemWriter<TopicClusteringResult> {

    private TopicAssignmentRepository assignmentRepository;

    private TopicRepository topicRepository;

    @Autowired
    public TopicWriter(MongoTemplate mongoTemplate, TopicAssignmentRepository assignmentRepository, TopicRepository topicRepository) {
        this.assignmentRepository = assignmentRepository;
        this.topicRepository = topicRepository;
    }

    @Override
    public void write(List<? extends TopicClusteringResult> items) {
        if (items.isEmpty()) {
            return;
        }
        TopicClusteringResult topicClusteringResult = items.get(0);
        Collection<TopicAssignment> assignments = topicClusteringResult.getAssignments();
        List<Topic> topics = topicClusteringResult.getTopics();
        assignmentRepository.saveAll(assignments);
        topicRepository.saveAll(topics);
    }
}
