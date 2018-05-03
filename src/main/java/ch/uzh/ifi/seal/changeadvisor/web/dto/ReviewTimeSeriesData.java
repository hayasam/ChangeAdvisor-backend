package ch.uzh.ifi.seal.changeadvisor.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReviewTimeSeriesData implements Comparable<ReviewTimeSeriesData> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private Date reviewDate;

    private List<Integer> ratings;

    private int reviewCount;

    private double average;

    public ReviewTimeSeriesData(Date reviewDate, List<Integer> ratings) {
        this.reviewDate = reviewDate;
        this.ratings = ratings;
        this.reviewCount = ratings.size();
        this.average = getAverage();
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    @JsonIgnore
    public List<Integer> getRatings() {
        return ratings;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public long reviewCountByRating(int rating) {
        return ratings.stream().filter(stars -> stars.equals(rating)).count();
    }

    public double getAverage() {
        int sum = ratings.stream().mapToInt(stars -> stars).sum();
        return sum / (double) ratings.size();
    }

    @Override
    public int compareTo(@NotNull ReviewTimeSeriesData o) {
        return reviewDate.compareTo(o.reviewDate);
    }

    @Override
    public String toString() {
        return "ReviewTimeSeriesData{" +
                "reviewDate=" + dateFormat.format(reviewDate) +
                ", average=" + average +
                ", reviewCount=" + reviewCount +
                '}';
    }
}
