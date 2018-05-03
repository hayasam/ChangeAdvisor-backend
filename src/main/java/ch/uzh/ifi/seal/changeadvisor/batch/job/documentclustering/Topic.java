package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by alex on 24.07.2017.
 */
@Document
public class Topic {

    @Id
    private String id;

    private final Set<String> bag;

    private final int topicId;

    private LocalDateTime timestamp;

    public Topic(Set<String> bag, int topic) {
        this.bag = bag;
        this.topicId = topic;
        timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Set<String> getBag() {
        return bag;
    }

    public int getTopicId() {
        return topicId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "bag=" + bag +
                ", topic=" + topicId +
                ", timestamp=" + timestamp +
                '}';
    }
}
