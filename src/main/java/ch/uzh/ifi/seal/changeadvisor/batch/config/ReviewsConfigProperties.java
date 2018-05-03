package ch.uzh.ifi.seal.changeadvisor.batch.config;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("datasource.reviews")
public class ReviewsConfigProperties {

    private MongoProperties mongodb = new MongoProperties();

    public MongoProperties getMongodb() {
        return mongodb;
    }
}
