package ch.uzh.ifi.seal.changeadvisor.batch.config;

import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewNoOpMarker;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackageClasses = ReviewNoOpMarker.class, mongoTemplateRef = "reviewsOperations")
public class ReviewsDbConfiguration extends AbstractRepoConfig {

    @Bean
    public ReviewsConfigProperties reviewsDataSource() {
        return new ReviewsConfigProperties();
    }

    @Override
    protected String getDatabaseName() {
        final MongoClientURI mongoClientURI = new MongoClientURI(reviewsDataSource().getMongodb().getUri());
        return mongoClientURI.getDatabase();
    }

    @Override
    protected MongoClientURI getPropertiesMongoUri() {
        return new MongoClientURI(reviewsDataSource().getMongodb().getUri());
    }

    @Override
    public MongoClient mongo() {
        final MongoClientURI mongoClientURI = new MongoClientURI(reviewsDataSource().getMongodb().getUri());
        return new MongoClient(mongoClientURI.getHosts().get(0), reviewsDataSource().getMongodb().getPort());
    }

    @Bean(name = {"reviewsOperations"})
    @Override
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongo(), getDatabaseName());
    }
}
