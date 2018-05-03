package ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkableReview;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by alex on 20.07.2017.
 */
@Document
public class TransformedFeedback implements LinkableReview, Comparable<TransformedFeedback> {

    @Id
    private String id;

    private ArdocResult ardocResult;

    private Collection<String> bagOfWords;

    private String transformedSentence;

    private LocalDateTime timestamp;

    public TransformedFeedback() {
    }

    public TransformedFeedback(ArdocResult ardocResult, Collection<String> tokens) {
        this.ardocResult = ardocResult;
        this.bagOfWords = tokens;
        this.transformedSentence = String.join(" ", tokens);
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public ArdocResult getArdocResult() {
        return ardocResult;
    }

    public Collection<String> getBagOfWords() {
        return bagOfWords;
    }

    public List<String> getBagOfWordsAsList() {
        return new ArrayList<>(bagOfWords);
    }

    public String getBagAsString() {
        return String.join(" ", bagOfWords);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getSentence() {
        return ardocResult.getSentence();
    }

    public String getCategory() {
        return ardocResult.getCategory();
    }

    public String getTransformedSentence() {
        return transformedSentence;
    }

    public Review getReview() {
        return ardocResult.getReview();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setArdocResult(ArdocResult ardocResult) {
        this.ardocResult = ardocResult;
    }

    public void setBagOfWords(Collection<String> bagOfWords) {
        this.bagOfWords = bagOfWords;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setTransformedSentence(String transformedSentence) {
        this.transformedSentence = transformedSentence;
    }

    @Override
    public Set<String> getBag() {
        return ImmutableSet.copyOf(getBagOfWords());
    }

    @Override
    public String getOriginalSentence() {
        return getSentence();
    }

    @Override
    public String toString() {
        return "TransformedFeedback{" +
                "sentence=" + ardocResult.getSentence() +
                ", bagOfWords=" + bagOfWords +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransformedFeedback feedback = (TransformedFeedback) o;

        if (ardocResult != null ? !ardocResult.equals(feedback.ardocResult) : feedback.ardocResult != null)
            return false;
        return transformedSentence != null ? transformedSentence.equals(feedback.transformedSentence) : feedback.transformedSentence == null;
    }

    @Override
    public int hashCode() {
        int result = ardocResult != null ? ardocResult.hashCode() : 0;
        result = 31 * result + (transformedSentence != null ? transformedSentence.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(@NotNull TransformedFeedback o) {
        return ardocResult.getReviewDate().compareTo(o.getArdocResult().getReviewDate());
    }
}
