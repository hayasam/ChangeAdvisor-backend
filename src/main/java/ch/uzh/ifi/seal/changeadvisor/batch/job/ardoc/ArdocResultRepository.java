package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unused")
@Repository
public interface ArdocResultRepository extends MongoRepository<ArdocResult, String> {

    List<ArdocResult> findByAppName(String appName);

    Page<ArdocResult> findByAppName(String appName, Pageable pageable);

    Page<ArdocResult> findByAppNameAndTimestampGreaterThan(String appName, LocalDateTime timestamp, Pageable pageable);

    List<ArdocResult> findByAppNameOrderByReview_ReviewDateDesc(String appName);

    List<ArdocResult> findTop1ByAppNameOrderByReview_ReviewDateDesc(String appName);

    List<ArdocResult> findByAppNameAndCategoryAndSentenceContainingIgnoreCase(String appName, String category, String label);
}
