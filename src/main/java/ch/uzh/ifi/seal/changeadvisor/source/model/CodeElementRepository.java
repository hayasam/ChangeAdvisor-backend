package ch.uzh.ifi.seal.changeadvisor.source.model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Represents a code component. A set of words and the code component they are derived from.
 * Created by alex on 14.07.2017.
 */
@Repository
public interface CodeElementRepository extends MongoRepository<CodeElement, String> {

    List<CodeElement> findByAppName(String appName);

    void deleteByAppName(String appName);
}
