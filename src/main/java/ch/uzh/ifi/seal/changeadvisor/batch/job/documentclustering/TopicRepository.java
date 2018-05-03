package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by alex on 24.07.2017.
 */
@Repository
public interface TopicRepository extends MongoRepository<Topic, String> {

}