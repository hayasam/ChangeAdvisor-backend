package ch.uzh.ifi.seal.changeadvisor.web.dto;

import ch.uzh.ifi.seal.changeadvisor.batch.job.HasReview;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.Label;
import com.google.common.collect.ImmutableList;
import org.springframework.util.Assert;

import java.util.List;

public class LabelWithReviews {

    private final String label;

    private final double score;

    private final List<HasReview> reviews;

    public <T extends HasReview> LabelWithReviews(Label label, List<T> reviews) {
        Assert.notNull(reviews, "Reviews cannot be null! In case of no results make sure to pass an empty list.");
        Assert.notNull(label, "Label cannot be null!");
        this.label = label.getLabel();
        this.score = label.getScore();
        this.reviews = ImmutableList.copyOf(reviews);
    }

    public String getLabel() {
        return label;
    }

    public double getScore() {
        return score;
    }

    public int getReviewCount() {
        return reviews.size();
    }

    public List<HasReview> getReviews() {
        return reviews;
    }

    @Override
    public String toString() {
        return "LabelWithReviews{" +
                "label='" + label + '\'' +
                ", reviews=" + reviews +
                '}';
    }
}
