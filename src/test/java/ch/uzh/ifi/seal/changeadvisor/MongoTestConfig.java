package ch.uzh.ifi.seal.changeadvisor;

import ch.uzh.ifi.seal.changeadvisor.batch.config.AbstractRepoConfig;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkingResultRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.LabelRepository;
import ch.uzh.ifi.seal.changeadvisor.project.ProjectRepository;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElementRepository;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackageClasses =
        {CodeElementRepository.class, TopicRepository.class,
                TransformedFeedbackRepository.class, ArdocResultRepository.class, LinkingResultRepository.class,
                LabelRepository.class, ReviewRepository.class, ProjectRepository.class}, mongoTemplateRef = "testMongoTemplate")
public class MongoTestConfig extends AbstractRepoConfig {

    @Bean
    @Profile("test")
    public MongoDbFactory mongoDbFactory() {
        return new SimpleMongoDbFactory(new MongoClient(), "changeAdvisorTest");
    }

    @Override
    protected String getDatabaseName() {
        return "changeAdvisorTest";
    }

    @Override
    protected MongoClientURI getPropertiesMongoUri() {
        return new MongoClientURI("mongodb://localhost/changeAdvisorTest");
    }

    @Override
    public Mongo mongo() {
        return new MongoClient(null, 27017);
    }

    @Bean
    public MongoTemplate testMongoTemplate() {
        return new MongoTemplate(mongoDbFactory());
    }
}
