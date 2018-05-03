package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArdocService {

    private static final Logger logger = LoggerFactory.getLogger(ArdocService.class);

    private final ArdocResultRepository repository;

    private final ReviewRepository reviewRepository;

    public ArdocService(ArdocResultRepository repository, ReviewRepository reviewRepository) {
        this.repository = repository;
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getReviewsSinceLastAnalyzed(final String app) {
        ArdocResult lastAnalyzed = getLastAnalyzed(app);
        List<Review> reviewsSinceLastAnalyzed;
        if (lastAnalyzed != null) {
            reviewsSinceLastAnalyzed = reviewRepository
                    .findByAppNameAndReviewDateGreaterThanOrderByReviewDateDesc(app, lastAnalyzed.getReviewDate());
            logger.info(
                    String.format("Found %d reviews to analyze since last ardoc run. Review Date: %s.\tArdoc timestamp: %s.",
                            reviewsSinceLastAnalyzed.size(), lastAnalyzed.getReviewDate(), lastAnalyzed.getTimestamp()));
        } else {
            reviewsSinceLastAnalyzed = reviewRepository.findByAppName(app);
            logger.info(
                    String.format("No reviews yet analyzed. Found %d reviews to analyze.", reviewsSinceLastAnalyzed.size()));
        }
        return reviewsSinceLastAnalyzed;
    }

    public ArdocResult getLastAnalyzed(final String app) {
        List<ArdocResult> res = repository.findTop1ByAppNameOrderByReview_ReviewDateDesc(app);
        return res.isEmpty() ? null : res.get(0);
    }
}
