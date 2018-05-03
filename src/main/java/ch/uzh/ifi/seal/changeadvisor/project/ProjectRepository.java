package ch.uzh.ifi.seal.changeadvisor.project;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    Project findByAppName(String appName);

    boolean existsByAppName(String appName);
}
