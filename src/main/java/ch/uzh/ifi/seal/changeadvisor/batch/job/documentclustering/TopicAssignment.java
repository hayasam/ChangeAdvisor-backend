package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkableReview;
import com.google.common.collect.Sets;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

/**
 * Created by alex on 24.07.2017.
 */
@Document
public class TopicAssignment implements LinkableReview {

    @Id
    private String id;

    private String originalSentence;

    private Set<String> bag;

    private int topic;

    private LocalDateTime timestamp;

    TopicAssignment() {
    }

    public TopicAssignment(String originalSentence, Collection<String> bag, int topic) {
        this.originalSentence = originalSentence;
        this.bag = Sets.newHashSet(bag);
        this.topic = topic;
        timestamp = LocalDateTime.now();
    }

    public TopicAssignment(Set<String> bag, int topic) {
        this.originalSentence = "";
        this.bag = bag;
        this.topic = topic;
        timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    @Override
    public String getOriginalSentence() {
        return originalSentence;
    }

    @Override
    public Set<String> getBag() {
        return Sets.newHashSet(bag);
    }

    public int getTopic() {
        return topic;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "TopicAssignment{" +
                "originalSentence='" + originalSentence + '\'' +
                ", bag=" + bag +
                ", topic=" + topic +
                ", timestamp=" + timestamp +
                '}';
    }
}
