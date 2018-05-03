package ch.uzh.ifi.seal.changeadvisor.batch.job.reviews;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByAppName(String appName);

    Page<Review> findByAppName(String appName, Pageable pageable);

    List<Review> findByAppNameAndReviewDateGreaterThanOrderByReviewDateDesc(String appName, Date reviewDate);

    List<Review> findByAppNameAndReviewTextContainingIgnoreCase(String appName, String label);

    long deleteAllByAppName(String appName);
}
