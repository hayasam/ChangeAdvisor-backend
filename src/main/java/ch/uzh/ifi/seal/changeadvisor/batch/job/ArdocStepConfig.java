package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocProcessor;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResults;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultsWriter;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ReviewProcessor;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewReader;
import ch.uzh.ifi.seal.changeadvisor.service.ArdocService;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
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

    private static final String TEST_DIRECTORY = "test_files_parser";

    private static final String FROSTWIRE_REVIEW = "com.frostwire.android.200subset.txt";

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

    @Bean
    public Step ardocAnalysis() {
        return stepBuilderFactory.get(STEP_NAME)
                .<String, ArdocResults>chunk(10)
                .reader(reviewReader())
                .processor(ardocProcessor())
                .writer(ardocWriter)
                .build();
    }

    public Step ardocAnalysis(final String appName) {
        return stepBuilderFactory.get(STEP_NAME)
                .<Review, ArdocResults>chunk(10)
                .reader(reviewReader(appName))
                .processor(reviewProcessor())
                .writer(ardocWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<String> reviewReader() {
        FlatFileItemReader<String> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(TEST_DIRECTORY + "/" + FROSTWIRE_REVIEW));
        reader.setLineMapper(new PassThroughLineMapper());
        return reader;
    }

    private ReviewReader reviewReader(String app) {
        return new ReviewReader(ardocService, app);
    }

    @Bean
    public ItemProcessor<String, ArdocResults> ardocProcessor() {
        return new ArdocProcessor();
    }

    @Bean
    public ItemProcessor<Review, ArdocResults> reviewProcessor() {
        return new ReviewProcessor();
    }
}
