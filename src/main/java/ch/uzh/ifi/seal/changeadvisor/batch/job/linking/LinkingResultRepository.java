package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkingResultRepository extends MongoRepository<LinkingResult, String> {

    long deleteByAppName(String appName);
}
