package ch.uzh.ifi.seal.changeadvisor.batch.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClientURI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
public abstract class AbstractRepoConfig {

    protected abstract String getDatabaseName();

    protected abstract MongoClientURI getPropertiesMongoUri();

    public abstract Mongo mongo();

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDbFactory());
    }

    @Bean
    public MongoDbFactory mongoDbFactory() {
        return new SimpleMongoDbFactory(getPropertiesMongoUri());
    }
}
