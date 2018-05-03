package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArdocResultRepository extends MongoRepository<ArdocResult, String> {

    List<ArdocResult> findByAppName(String appName);

    Page<ArdocResult> findByAppName(String appName, Pageable pageable);

    List<ArdocResult> findTop1ByAppNameOrderByReview_ReviewDateDesc(String appName);
}
