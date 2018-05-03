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

    private final int topic;

    private LocalDateTime timestamp;

    public Topic(Set<String> bag, int topic) {
        this.bag = bag;
        this.topic = topic;
        timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Set<String> getBag() {
        return bag;
    }

    public int getTopic() {
        return topic;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "bag=" + bag +
                ", topic=" + topic +
                ", timestamp=" + timestamp +
                '}';
    }
}
