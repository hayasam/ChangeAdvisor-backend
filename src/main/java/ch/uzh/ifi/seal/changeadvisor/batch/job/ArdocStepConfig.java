package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResults;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultsWriter;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ReviewProcessor;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewReader;
import ch.uzh.ifi.seal.changeadvisor.service.ArdocService;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Configuration of Ardoc step of ChangeAdvisor.
 * Created by alex on 17.07.2017.
 *
 * @see SourceComponentsTransformationStepConfig
 * @see ArdocStepConfig
 */
@Component
public class ArdocStepConfig {

    private static final String STEP_NAME = "ardoc";

    private final StepBuilderFactory stepBuilderFactory;

    private final ArdocResultsWriter ardocWriter;

    private final ArdocService ardocService;

    @Autowired
    public ArdocStepConfig(StepBuilderFactory stepBuilderFactory,
                           ArdocResultsWriter ardocWriter, ArdocService ardocService) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.ardocWriter = ardocWriter;
        this.ardocService = ardocService;
    }

    public Step ardocAnalysis(final String appName) {
        return stepBuilderFactory.get(STEP_NAME)
                .<Review, ArdocResults>chunk(10)
                .reader(reviewReader(appName))
                .processor(reviewProcessor())
                .writer(ardocWriter)
                .build();
    }

    private ReviewReader reviewReader(String app) {
        return new ReviewReader(ardocService, app);
    }

    @Bean
    public ItemProcessor<Review, ArdocResults> reviewProcessor() {
        return new ReviewProcessor();
    }
}
