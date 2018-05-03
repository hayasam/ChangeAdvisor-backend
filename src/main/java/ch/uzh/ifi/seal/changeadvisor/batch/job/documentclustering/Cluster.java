package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkableReview;

import java.util.Collection;
import java.util.UUID;

public class Cluster {

    private String topicId;

    private Collection<? extends LinkableReview> reviews;

    public Cluster(Collection<? extends LinkableReview> assignments) {
        this.topicId = UUID.randomUUID().toString();
        this.reviews = assignments;
    }

    public Cluster(String topicId, Collection<? extends LinkableReview> reviews) {
        this.topicId = topicId;
        this.reviews = reviews;
    }

    public String getTopicId() {
        return topicId;
    }

    public Collection<? extends LinkableReview> getReviews() {
        return reviews;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "topicId=" + topicId +
                ", assignments=" + reviews +
                '}';
    }
}
