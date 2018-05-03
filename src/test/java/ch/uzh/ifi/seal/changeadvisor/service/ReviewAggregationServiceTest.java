package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.ChangeadvisorApplication;
import ch.uzh.ifi.seal.changeadvisor.MongoTestConfig;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewRepository;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewCategoryReport;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewDistributionReport;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewTimeSeriesData;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ChangeadvisorApplication.class, MongoTestConfig.class})
@ActiveProfiles("test")
public class ReviewAggregationServiceTest {

    private static final int MIN_STARS = 1;

    private static final int MAX_STARS = 5;

    private ReviewAggregationService service;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TransformedFeedbackRepository feedbackRepository;

    @Autowired
    private ArdocResultRepository ardocResultRepository;

    @Autowired
    private MongoTemplate testMongoTemplate;

    private static final String APP_NAME = "test.app";

    private List<TransformedFeedback> featureReq;

    private List<TransformedFeedback> infoSeeking;

    private List<TransformedFeedback> problem;

    private List<TransformedFeedback> infoG;

    private List<TransformedFeedback> other;

    @Before
    public void setUp() throws Exception {
        reviewRepository.deleteAllByAppName(APP_NAME);
        feedbackRepository.deleteByArdocResultAppName(APP_NAME);

        featureReq = createFeedbacks("FEATURE REQUEST");
        infoSeeking = createFeedbacks("INFORMATION SEEKING");
        problem = createFeedbacks("PROBLEM DISCOVERY");
        infoG = createFeedbacks("INFORMATION GIVING");
        other = createFeedbacks("OTHER");

        service = new ReviewAggregationService(testMongoTemplate, testMongoTemplate, null, feedbackRepository, null);
    }

    private List<TransformedFeedback> createFeedbacks(final String category) {
        List<TransformedFeedback> feedbacks = new ArrayList<>();
        for (int i = 0; i < new Random().nextInt(10) + 50; i++) {
            TransformedFeedback t = new TransformedFeedback();
            ArdocResult result = new ArdocResult();
            result.setCategory(category);
            result.setAppName(APP_NAME);
            t.setArdocResult(result);
            t.setTransformedSentence(UUID.randomUUID().toString());
            feedbacks.add(t);
        }

        feedbacks.forEach(f -> f.setArdocResult(ardocResultRepository.save(f.getArdocResult())));
        return feedbackRepository.saveAll(feedbacks);
    }

    @Test
    public void timeseries() throws Exception {
        Map<Date, Map<Integer, List<Review>>> dateRatingMap = createReviews();
        List<ReviewTimeSeriesData> timeSeries = service.timeSeries(APP_NAME);

        Assert.assertThat(timeSeries.size(), is(5));

        for (ReviewTimeSeriesData point : timeSeries) {
            Date reviewDate = point.getReviewDate();

            Map<Integer, List<Review>> ratingReviewsMap = dateRatingMap.get(reviewDate);

            int sum = 0;
            int totalReviews = 0;
            for (int i = MIN_STARS; i <= MAX_STARS; i++) {
                long numberOfReviewWithRatingN = point.reviewCountByRating(i);
                List<Review> reviewsWithRatingN = ratingReviewsMap.get(i);

                Assert.assertThat(numberOfReviewWithRatingN, is((long) reviewsWithRatingN.size()));

                // for average assert coming up next.
                sum += reviewsWithRatingN.size() * i;
                totalReviews += reviewsWithRatingN.size();
            }

            double averageOfReviews = point.getAverage();

            Assert.assertEquals(sum / (double) totalReviews, averageOfReviews, 0.01);
        }
    }

    private Map<Date, Map<Integer, List<Review>>> createReviews() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date firstDay = dateFormat.parse("01-11-17");
        Date secondDay = dateFormat.parse("02-11-17");
        Date thirdDay = dateFormat.parse("03-11-17");
        Date fourthDay = dateFormat.parse("04-11-17");
        Date fifthDay = dateFormat.parse("05-11-17");
        List<Date> days = Lists.newArrayList(firstDay, secondDay, thirdDay, fourthDay, fifthDay);

        Map<Date, Map<Integer, List<Review>>> dateRatingMap = new HashMap<>();
        days.forEach(day -> dateRatingMap.put(day, new HashMap<>()));

        for (Date day : days) {
            for (int i = MIN_STARS; i <= MAX_STARS; i++) {
                List<Review> reviews = createReviews(i, day);
                reviews = reviewRepository.saveAll(reviews);
                dateRatingMap.get(day).put(i, reviews);
            }
        }

        return dateRatingMap;
    }

    private List<Review> createReviews(int rating, Date date) {
        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < new Random().nextInt(10) + 1; i++) {
            reviews.add(new Review(APP_NAME, UUID.randomUUID().toString(), date, rating));
        }
        return reviews;
    }

    @Test
    public void groupyBy() throws Exception {
        ReviewDistributionReport reviewCategories = service.groupByCategories(APP_NAME);
        ReviewCategoryReport featureRequest = reviewCategories.findForCategory("FEATURE REQUEST");
        ReviewCategoryReport informationSeeking = reviewCategories.findForCategory("INFORMATION SEEKING");
        ReviewCategoryReport problemDiscovery = reviewCategories.findForCategory("PROBLEM DISCOVERY");
        ReviewCategoryReport informationGiving = reviewCategories.findForCategory("INFORMATION GIVING");
        ReviewCategoryReport other = reviewCategories.findForCategory("OTHER");

        Assert.assertThat(featureRequest.getReviewCount(), is(this.featureReq.size()));
        Assert.assertThat(informationSeeking.getReviewCount(), is(this.infoSeeking.size()));
        Assert.assertThat(problemDiscovery.getReviewCount(), is(this.problem.size()));
        Assert.assertThat(informationGiving.getReviewCount(), is(this.infoG.size()));
        Assert.assertThat(other.getReviewCount(), is(this.other.size()));
    }

    @Test
    public void groupyByCountOnly() {
        ReviewDistributionReport reviewCategories = service.groupByCategoriesCountOnly(APP_NAME);
        ReviewCategoryReport featureRequest = reviewCategories.findForCategory("FEATURE REQUEST");
        ReviewCategoryReport informationSeeking = reviewCategories.findForCategory("INFORMATION SEEKING");
        ReviewCategoryReport problemDiscovery = reviewCategories.findForCategory("PROBLEM DISCOVERY");
        ReviewCategoryReport informationGiving = reviewCategories.findForCategory("INFORMATION GIVING");
        ReviewCategoryReport other = reviewCategories.findForCategory("OTHER");

        Assert.assertThat(featureRequest.getReviewCount(), is(this.featureReq.size()));
        Assert.assertThat(informationSeeking.getReviewCount(), is(this.infoSeeking.size()));
        Assert.assertThat(problemDiscovery.getReviewCount(), is(this.problem.size()));
        Assert.assertThat(informationGiving.getReviewCount(), is(this.infoG.size()));
        Assert.assertThat(other.getReviewCount(), is(this.other.size()));
    }

}