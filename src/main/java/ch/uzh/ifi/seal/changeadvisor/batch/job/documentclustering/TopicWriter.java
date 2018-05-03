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

    private static final String COLLECTION_NAME = "topicAssignment";

    private MongoItemWriter<TopicAssignment> writer;

    private TopicAssignmentRepository assignmentRepository;

    private TopicRepository topicRepository;

    @Autowired
    public TopicWriter(MongoTemplate mongoTemplate, TopicAssignmentRepository assignmentRepository, TopicRepository topicRepository) {
        this.assignmentRepository = assignmentRepository;
        this.topicRepository = topicRepository;
        writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection(COLLECTION_NAME);
    }

    @Override
    public void write(List<? extends TopicClusteringResult> items) throws Exception {
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
