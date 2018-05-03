package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkableReview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class Cluster {

    private String topicId;

    private Collection<LinkableReview> reviews;

    public Cluster(Collection<? extends LinkableReview> assignments) {
        this(UUID.randomUUID().toString(), assignments);
    }

    public Cluster(String topicId, Collection<? extends LinkableReview> reviews) {
        this.topicId = topicId;
        this.reviews = new ArrayList<>(reviews);
    }

    public String getTopicId() {
        return topicId;
    }

    public Collection<LinkableReview> getReviews() {
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
