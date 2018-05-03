package ch.uzh.ifi.seal.changeadvisor.web.dto;

import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.tfidf.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReviewCategory implements ReviewCategoryReport {

    private static final Tokenizer tokenizer = new Tokenizer();

    private static final String WHITESPACE = " ";

    private final Set<TransformedFeedback> reviews;

    private final String category;

    public ReviewCategory(Collection<TransformedFeedback> reviews, String category) {
        this.reviews = ImmutableSet.copyOf(reviews);
        this.category = category;
    }

    @JsonIgnore
    public Set<TransformedFeedback> getReviews() {
        return reviews;
    }

    @Override
    public String getCategory() {
        return category;
    }

    public int size() {
        return reviews.size();
    }

    @Override
    public int getReviewCount() {
        return size();
    }

    public Document asDocument(int n) {
        if (n == 1) {
            List<String> tokens = tokenizer.tokenize(aggregateReviewsIntoDocument());
            List<AbstractNGram> unigrams = tokens.stream().map(Unigram::new).collect(Collectors.toList());
            return new Document(unigrams);
        } else {
            List<List<String>> tokens = tokenizer.tokenize(aggregateReviewsIntoDocument(), n);
            List<AbstractNGram> ngrams = tokens.stream().map(NGram::new).collect(Collectors.toList());
            return new Document(ngrams);
        }
    }

    private String aggregateReviewsIntoDocument() {
        List<String> sentences = reviews.stream().map(TransformedFeedback::getTransformedSentence).collect(Collectors.toList());
        return String.join(WHITESPACE, sentences);
    }

    @Override
    public String toString() {
        return "ReviewCategory{" +
                "category='" + category + '\'' +
                "count=" + size() +
                '}';
    }
}
