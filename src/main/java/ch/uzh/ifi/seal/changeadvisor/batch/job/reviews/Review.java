package ch.uzh.ifi.seal.changeadvisor.batch.job.reviews;

import ch.uzh.ifi.seal.changeadvisor.batch.job.HasReview;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Review")
public class Review implements Comparable<Review>, HasReview {

    @Id
    private String id;

    private String appName;

    private String reviewText;

    private Date reviewDate;

    private int numberOfStars;

    public Review() {
    }

    public Review(String appName) {
        this.appName = appName;
    }

    public Review(String appName, String reviewText, Date reviewDate, int numberOfStars) {
        this.appName = appName;
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
        this.numberOfStars = numberOfStars;
    }

    public Review(String id, String appName, String reviewText, Date reviewDate, int numberOfStars) {
        this.id = id;
        this.appName = appName;
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
        this.numberOfStars = numberOfStars;
    }

    public String getId() {
        return id;
    }

    public String getAppName() {
        return appName;
    }

    @Override
    public String getReviewText() {
        return reviewText;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public int getNumberOfStars() {
        return numberOfStars;
    }

    /**
     * Sort in descending order.
     *
     * @param o other.
     * @return
     */
    @Override
    public int compareTo(@NotNull Review o) {
        return o.getReviewDate().compareTo(reviewDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Review review = (Review) o;

        if (numberOfStars != review.numberOfStars) return false;
        if (appName != null ? !appName.equals(review.appName) : review.appName != null) return false;
        if (reviewText != null ? !reviewText.equals(review.reviewText) : review.reviewText != null) return false;
        return reviewDate != null ? reviewDate.equals(review.reviewDate) : review.reviewDate == null;
    }

    @Override
    public int hashCode() {
        int result = appName != null ? appName.hashCode() : 0;
        result = 31 * result + (reviewText != null ? reviewText.hashCode() : 0);
        result = 31 * result + (reviewDate != null ? reviewDate.hashCode() : 0);
        result = 31 * result + numberOfStars;
        return result;
    }
}
