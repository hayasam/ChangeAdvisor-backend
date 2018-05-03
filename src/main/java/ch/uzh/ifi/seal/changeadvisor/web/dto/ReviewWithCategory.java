package ch.uzh.ifi.seal.changeadvisor.web.dto;

import ch.uzh.ifi.seal.changeadvisor.batch.job.HasReview;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class ReviewWithCategory implements Comparable<ReviewWithCategory>, HasReview {

    private final String reviewId;

    private final Date reviewDate;

    private final String review;

    private final int numberOfStars;

    private final String category;

    public ReviewWithCategory(Review review, String category) {
        this.reviewId = review.getId();
        this.reviewDate = review.getReviewDate();
        this.review = review.getReviewText();
        this.numberOfStars = review.getNumberOfStars();
        this.category = category;
    }

    @Override
    public String getReviewText() {
        return review;
    }

    public String getCategory() {
        return category;
    }

    public String getReviewId() {
        return reviewId;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public int getNumberOfStars() {
        return numberOfStars;
    }

    @Override
    public String toString() {
        return "ReviewWithCategory{" +
                "review=" + review +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReviewWithCategory that = (ReviewWithCategory) o;

        if (numberOfStars != that.numberOfStars) return false;
        if (reviewId != null ? !reviewId.equals(that.reviewId) : that.reviewId != null) return false;
        if (reviewDate != null ? !reviewDate.equals(that.reviewDate) : that.reviewDate != null) return false;
        if (review != null ? !review.equals(that.review) : that.review != null) return false;
        return category != null ? category.equals(that.category) : that.category == null;
    }

    @Override
    public int hashCode() {
        int result = reviewId != null ? reviewId.hashCode() : 0;
        result = 31 * result + (reviewDate != null ? reviewDate.hashCode() : 0);
        result = 31 * result + (review != null ? review.hashCode() : 0);
        result = 31 * result + numberOfStars;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(@NotNull ReviewWithCategory o) {
        return review.compareTo(o.review);
    }
}
