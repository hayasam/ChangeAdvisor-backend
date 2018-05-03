package ch.uzh.ifi.seal.changeadvisor.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a request to retrieve the reviews for the top N labels and the
 * information based on which the top N label are computed.
 */
public class ReviewsByTopLabelsDto {

    /**
     * The name of the app for which we want to retrieve reviews.
     */
    private final String app;

    /**
     * Google play id of the app.
     */
    private final String googlePlayId;

    /**
     * The ardoc category for which we want to retrieve reviews.
     */
    private final String category;

    /**
     * Limit of n labels.
     */
    private final int limit;

    /**
     * Size of ngram for a label.
     */
    private final int ngrams;

    public ReviewsByTopLabelsDto(@JsonProperty(value = "app", required = true) String app,
                                 @JsonProperty(value = "googlePlayId", required = false) String googlePlayId,
                                 @JsonProperty(value = "category") String category,
                                 @JsonProperty(value = "limit") int limit,
                                 @JsonProperty(value = "ngrams") int ngrams) {
        this.app = app;
        this.googlePlayId = googlePlayId;
        this.category = category;
        this.limit = limit < 1 ? 10 : limit;
        this.ngrams = ngrams < 1 ? 1 : ngrams;
    }

    public String getApp() {
        return app;
    }

    public String getGooglePlayId() {
        return googlePlayId;
    }

    public String getCategory() {
        return category;
    }

    public int getLimit() {
        return limit;
    }

    public int getNgrams() {
        return ngrams;
    }
}
