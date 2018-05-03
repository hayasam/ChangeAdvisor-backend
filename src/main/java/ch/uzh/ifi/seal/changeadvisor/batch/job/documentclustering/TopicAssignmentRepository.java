package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by alex on 24.07.2017.
 */
@Repository
public interface TopicAssignmentRepository extends MongoRepository<TopicAssignment, String> {

    List<TopicAssignment> findByTopic(Integer topic);
}
