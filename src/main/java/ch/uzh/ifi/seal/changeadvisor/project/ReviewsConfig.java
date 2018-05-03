package ch.uzh.ifi.seal.changeadvisor.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("unused")
public class ReviewsConfig implements Serializable {

    private final Date lastReviewImport;

    private final Date nextReviewImport;

    @JsonCreator
    public ReviewsConfig(@JsonProperty("lastReviewImport") Date lastReviewImport, @JsonProperty("nextReviewImport") Date nextReviewImport) {
        this.lastReviewImport = lastReviewImport;
        this.nextReviewImport = nextReviewImport;
    }

    public Date getLastReviewImport() {
        return lastReviewImport;
    }

    public Date getNextReviewImport() {
        return nextReviewImport;
    }

    public static ReviewsConfig of(ReviewsConfig config, Date nextReviewImport) {
        return new ReviewsConfig(config == null ? null : config.lastReviewImport, nextReviewImport);
    }

    @Override
    public String toString() {
        return "ReviewsConfig{" +
                "lastReviewImport=" + lastReviewImport +
                ", nextReviewImport=" + nextReviewImport +
                '}';
    }
}
