package ch.uzh.ifi.seal.changeadvisor.service;


import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.Label;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.LabelRepository;
import ch.uzh.ifi.seal.changeadvisor.tfidf.AbstractNGram;
import ch.uzh.ifi.seal.changeadvisor.tfidf.Corpus;
import ch.uzh.ifi.seal.changeadvisor.tfidf.Document;
import ch.uzh.ifi.seal.changeadvisor.tfidf.TfidfService;
import ch.uzh.ifi.seal.changeadvisor.web.dto.*;
import com.google.common.collect.ImmutableSet;
import edu.emory.mathcs.backport.java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.util.CloseableIterator;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewAggregationService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewAggregationService.class);

    private static final Set<String> ardocCategories = ImmutableSet
            .of("FEATURE REQUEST", "INFORMATION SEEKING", "INFORMATION GIVING", "PROBLEM DISCOVERY", "OTHER");

    private static final String REVIEW_APPNAME_FIELD = "appName";
    private static final String APPNAME_FIELD = "ardocResult.appName";
    private static final String CATEGORY_FIELD = "ardocResult.category";
    private static final String REVIEW_DATE_FIELD = "reviewDate";
    private static final String REVIEW_COUNT_ALIAS = "reviewCount";
    private static final String CATEGORY_ALIAS = "category";

    private final MongoTemplate mongoOperations;

    private final MongoTemplate reviewsOperations;

    private final TfidfService tfidfService;

    private final TransformedFeedbackRepository transformedFeedbackRepository;

    private final LabelRepository labelRepository;

    @Autowired
    public ReviewAggregationService(MongoTemplate mongoOperations, MongoTemplate reviewsOperations, TfidfService tfidfService,
                                    TransformedFeedbackRepository transformedFeedbackRepository,
                                    LabelRepository labelRepository) {
        this.mongoOperations = mongoOperations;
        this.reviewsOperations = reviewsOperations;
        this.tfidfService = tfidfService;
        this.transformedFeedbackRepository = transformedFeedbackRepository;
        this.labelRepository = labelRepository;
    }

    /**
     * Generates a category distribution report.
     * Groups reviews by ardoc category but returns only the count of each category.
     *
     * @param appName application for which we want to generate a report.
     * @return distribution report.
     * @see ReviewDistributionReport
     * @see ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult#category
     */
    public List<ReviewTimeSeriesData> timeSeries(final String appName) {
        AggregationOperation match = Aggregation.match(Criteria.where(REVIEW_APPNAME_FIELD).is(appName));
        AggregationOperation group = Aggregation.group(REVIEW_DATE_FIELD).first(REVIEW_DATE_FIELD).as(REVIEW_DATE_FIELD) // set group by field and save it as 'category' in resulting object.
                .push("numberOfStars").as("ratings");// push entire document to field 'reviews' in ReviewCategory.

        TypedAggregation<Review> categoryAggregation = Aggregation.newAggregation(
                Review.class,
                match,
                group
        );

        AggregationResults<ReviewTimeSeriesData> groupResults =
                reviewsOperations.aggregate(categoryAggregation, Review.class, ReviewTimeSeriesData.class);

        List<ReviewTimeSeriesData> timeSeriesData = new ArrayList<>(groupResults.getMappedResults());
        Collections.sort(timeSeriesData);
        return timeSeriesData;
    }

    /**
     * Generates a category distribution report.
     * Groups reviews by ardoc category but returns only the count of each category.
     *
     * @param appName application for which we want to generate a report.
     * @return distribution report.
     * @see ReviewDistributionReport
     * @see ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult#category
     */
    public ReviewDistributionReport groupByCategoriesCountOnly(final String appName) {
        AggregationOperation match = Aggregation.match(Criteria.where(APPNAME_FIELD).is(appName));
        AggregationOperation group = Aggregation.group(CATEGORY_FIELD).count().as(REVIEW_COUNT_ALIAS);
        AggregationOperation projection = Aggregation.project().and("_id").as(CATEGORY_ALIAS)
                .and(REVIEW_COUNT_ALIAS).as(REVIEW_COUNT_ALIAS);

        TypedAggregation<TransformedFeedback> categoryAggregation =
                Aggregation.newAggregation(TransformedFeedback.class,
                        match,
                        group,
                        projection
                );

        AggregationResults<ReviewCategoryCountOnly> groupResults =
                mongoOperations.aggregate(categoryAggregation, TransformedFeedback.class, ReviewCategoryCountOnly.class);

        return new ReviewDistributionReport(groupResults.getMappedResults());
    }

    /**
     * Generates a category distribution report.
     * Groups reviews by ardoc category.
     *
     * @param appName application for which we want to generate a report.
     * @return distribution report.
     * @see ReviewDistributionReport
     * @see ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult#category
     */

    public ReviewDistributionReport groupByCategories(final String appName) {
        List<ReviewCategory> categories;
        try {
            TypedAggregation<TransformedFeedback> categoryAggregation = Aggregation.newAggregation(TransformedFeedback.class,
                    Aggregation.match(Criteria.where(APPNAME_FIELD).is(appName)),
                    Aggregation.group(CATEGORY_FIELD).first(CATEGORY_FIELD).as(CATEGORY_ALIAS) // set group by field and save it as 'category' in resulting object.
                            .push("$$ROOT").as("reviews") // push entire document to field 'reviews' in ReviewCategory.
            ).withOptions(Aggregation.newAggregationOptions().cursorBatchSize(1).allowDiskUse(true).build());

            CloseableIterator<ReviewCategory> groupResults =
                    mongoOperations.aggregateStream(categoryAggregation, TransformedFeedback.class, ReviewCategory.class); // In case the resulting doc is > 16MB it will throw error.

            categories = new ArrayList<>();
            while (groupResults.hasNext()) {
                ReviewCategory category = groupResults.next();
                categories.add(category);
            }

            return new ReviewDistributionReport(categories);
        } catch (UncategorizedMongoDbException e) {
            logger.error("Reached BSON memory limits. Running queries one by one...", e);

            categories = new ArrayList<>();
            for (String category : ardocCategories) {
                List<TransformedFeedback> reviews =
                        transformedFeedbackRepository.findAllByArdocResultCategoryAndArdocResultAppName(category, appName);

                ReviewCategory reviewCategory = new ReviewCategory(reviews, category);
                categories.add(reviewCategory);
            }

        }

        return new ReviewDistributionReport(categories);
    }

    /**
     * Retrieves the reviews based on the top N labels.
     * Fetches all reviews which contain these top labels.
     *
     * @param dto object representing the parameters we use to compute the top N labels
     *            (e.g. how many labels and for which app)
     * @return reviews for the top N labels.
     */
    public List<LabelWithReviews> reviewsByTopNLabelsByCategory(ReviewsByTopLabelsDto dto) {
        final int limit = dto.getLimit();
        List<Label> labels = labelRepository.findByAppNameAndCategoryAndNgramSizeOrderByScoreDesc(dto.getApp(), dto.getCategory(), dto.getNgrams());
        labels = getLabelsUpTo(labels, limit);

        logger.info("Fetching reviews for top %d labels: %s", dto.getLimit(), labels);
        List<LabelWithReviews> labelWithReviews = new ArrayList<>(labels.size());
        for (Label label : labels) {
            List<TransformedFeedback> feedback = transformedFeedbackRepository.findDistinctByArdocResultAppNameAndArdocResultCategoryAndTransformedSentenceContainingIgnoreCase(dto.getApp(), dto.getCategory(), label.getLabel());
            // Two ardoc results could be mapped to the same review, so in this step we remove duplicate reviews.
            List<Review> reviews = feedback.stream().map(TransformedFeedback::getReview).distinct().collect(Collectors.toList());
            labelWithReviews.add(new LabelWithReviews(label, reviews));
        }

        return labelWithReviews;
    }

    private List<Label> getLabelsUpTo(List<Label> labels, final int limit) {
        final int labelCount = labels.size();
        List<Label> labelsUpTo;
        if (limit <= labelCount) {
            labelsUpTo = labels.subList(0, limit);
        } else {
            labelsUpTo = labels.subList(0, Math.min(10, labelCount));
        }
        return labelsUpTo;
    }

    /**
     * Retrieves the reviews based on the top N labels.
     * Fetches all reviews which contain these top labels.
     *
     * @param dto object representing the parameters we use to compute the top N labels
     *            (e.g. how many labels and for which app)
     * @return reviews for the top N labels.
     */
    public List<LabelWithReviews> reviewsByTopNLabels(ReviewsByTopLabelsDto dto) {
        final int limit = dto.getLimit();
        List<Label> labels = labelRepository.findByAppNameAndNgramSizeOrderByScoreDesc(dto.getGooglePlayId(), dto.getNgrams());
        labels = getLabelsUpTo(labels, limit);

        logger.info("Fetching reviews for top %d labels: %s", dto.getLimit(), labels);
        List<LabelWithReviews> labelWithReviews = new ArrayList<>(labels.size());
        for (Label label : labels) {
            List<TransformedFeedback> feedback = transformedFeedbackRepository.findByArdocResultAppNameAndTransformedSentenceContainingIgnoreCase(dto.getGooglePlayId(), label.getLabel());
            // Two ardoc results could be mapped to the same review, so in this step we remove duplicate reviews.
            List<ReviewWithCategory> reviews = feedback.stream().map(f -> new ReviewWithCategory(f.getReview(), f.getCategory())).distinct().collect(Collectors.toList());
            java.util.Collections.sort(reviews);
            labelWithReviews.add(new LabelWithReviews(label, reviews));
        }

        return labelWithReviews;
    }

    /**
     * Retrieves the top N labels for a set of reviews.
     * A label is an Ngram of tokens that are representative for a group of reviews.
     *
     * @param dto object representing the parameters we use to compute the top N labels
     *            (e.g. how many labels and for which app)
     * @return list of labels with their tfidf score.
     * @see ReviewsByTopLabelsDto
     */
    public List<Label> topNLabels(ReviewsByTopLabelsDto dto) {
        // READER
        ReviewDistributionReport reviewsByCategory = groupByCategories(dto.getApp());
        final String category = dto.getCategory();
        Assert.isTrue(reviewsByCategory.hasCategory(category), String.format("Unknown category %s", category));

        // PROCESSOR
        List<Label> tokensWithScore = getNgramTokensWithScore(reviewsByCategory, dto);

        Collections.sort(tokensWithScore, Collections.reverseOrder());

        // WRITER
        final int limit = dto.getLimit();
        if (limit >= tokensWithScore.size()) {
            return tokensWithScore;
        }
        return tokensWithScore.subList(0, limit);
    }

    private List<Label> getNgramTokensWithScore(ReviewDistributionReport reviewsByCategory, ReviewsByTopLabelsDto dto) {
        Map<String, Document> categoryDocumentMap = mapReviewsToDocuments(reviewsByCategory, dto.getNgrams());

        Corpus corpus = new Corpus(categoryDocumentMap.values());
        Document document = categoryDocumentMap.get(dto.getCategory());
        List<AbstractNGram> uniqueTokens = document.uniqueTokens();

        return tfidfService.computeTfidfScoreForTokens(dto.getApp(), dto.getCategory(), uniqueTokens, document, corpus);
    }

    private Map<String, Document> mapReviewsToDocuments(ReviewDistributionReport reviewDistribution, final int ngramSize) {
        Map<String, Document> categoryDocumentMap = new HashMap<>();
        for (ReviewCategoryReport reviewCategory : reviewDistribution) {
            categoryDocumentMap.put(reviewCategory.getCategory(), ((ReviewCategory) reviewCategory).asDocument(ngramSize));
        }
        return categoryDocumentMap;
    }
}
