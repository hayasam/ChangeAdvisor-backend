package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultsWriter;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.FeedbackProcessor;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.FeedbackWriter;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import ch.uzh.ifi.seal.changeadvisor.preprocessing.CorpusProcessor;
import com.google.common.collect.Lists;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alex on 20.07.2017.
 */
@Component
public class FeedbackTransformationStepConfig {

    private static final String STEP_NAME = "feedbackTransformation";

    private static final String TEST_DIRECTORY = "test_files_parser";

    private static final int THRESHOLD = 3;

    private final StepBuilderFactory stepBuilderFactory;

    private final MongoTemplate mongoTemplate;

    private final ArdocResultRepository ardocRepository;

    private final TransformedFeedbackRepository transformedFeedbackRepository;

    @Autowired
    public FeedbackTransformationStepConfig(StepBuilderFactory stepBuilderFactory, MongoTemplate mongoTemplate,
                                            ArdocResultRepository ardocRepository,
                                            TransformedFeedbackRepository transformedFeedbackRepository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.mongoTemplate = mongoTemplate;
        this.ardocRepository = ardocRepository;
        this.transformedFeedbackRepository = transformedFeedbackRepository;
    }

    @Bean
    public Step transformFeedback() {
        return stepBuilderFactory.get(STEP_NAME)
                .<ArdocResult, TransformedFeedback>chunk(10)
                .reader(feedbackReader())
                .processor(feedbackProcessor())
                .writer(writer())
                .build();
    }

    public Step transformFeedback(final String appName) {
        return stepBuilderFactory.get(STEP_NAME)
                .allowStartIfComplete(true)
                .<ArdocResult, TransformedFeedback>chunk(10)
                .reader(feedbackRepositoryReader(appName))
                .processor(feedbackProcessor())
                .writer(writer())
                .build();
    }

    @Bean
    public MongoItemReader<ArdocResult> feedbackReader() {
        MongoItemReader<ArdocResult> reader = new MongoItemReader<>();
        reader.setTemplate(mongoTemplate);
        reader.setCollection(ArdocResultsWriter.COLLECTION_NAME);
        reader.setQuery("{}");
        Map<String, Sort.Direction> sort = new HashMap<>();
        sort.put("_id", Sort.Direction.ASC);
        reader.setSort(sort);
        reader.setTargetType(ArdocResult.class);
        return reader;
    }

    private RepositoryItemReader<ArdocResult> feedbackRepositoryReader(final String appName) {
        List<TransformedFeedback> topResult = transformedFeedbackRepository.findTop1ByArdocResultAppNameOrderByTimestampDesc(appName);
        TransformedFeedback lastTransformedFeedback = topResult.size() == 1 ? topResult.get(0) : null;

        RepositoryItemReader<ArdocResult> reader = new RepositoryItemReader<>();
        reader.setRepository(ardocRepository);
        reader.setMethodName("findByAppNameAndTimestampGreaterThan");
        reader.setArguments(Lists.newArrayList(appName, lastTransformedFeedback != null ? lastTransformedFeedback.getArdocResult().getTimestamp() : LocalDateTime.now()));
        reader.setPageSize(100);
        Map<String, Sort.Direction> sort = new HashMap<>();
        sort.put("timestamp", Sort.Direction.ASC);
        reader.setSort(sort);
        return reader;
    }

    protected ItemProcessor<ArdocResult, TransformedFeedback> feedbackProcessor() {
        return new FeedbackProcessor(corpusProcessor(), THRESHOLD);
    }

    private CorpusProcessor corpusProcessor() {
        return new CorpusProcessor.Builder()
                .escapeSpecialChars()
//                .withAutoCorrect(new EnglishSpellChecker())
                .withContractionExpander()
                .removeDuplicates(false) // For label computing we want to keep duplicates in order to keep the original sentence structure (specially for ngrams)
                .singularize()
                .removeStopWords()
                .posFilter()
                .stem()
                .removeTokensShorterThan(3)
                .build();
    }

    @Bean
    public ItemWriter<TransformedFeedback> writer() {
        return new FeedbackWriter(mongoTemplate);
    }

    @Bean
    public FlatFileItemWriter<TransformedFeedback> fileWriter() {
        FlatFileItemWriter<TransformedFeedback> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(TEST_DIRECTORY + "/transformed_feedback.csv"));
        writer.setHeaderCallback(headerWriter -> headerWriter.write("sentence,category,transformed_sentence"));
        writer.setLineAggregator(lineAggregator());
        return writer;
    }

    @Bean
    public LineAggregator<TransformedFeedback> lineAggregator() {
        return item -> item.getSentence() + "," + item.getCategory() + "," + item.getBagAsString();
    }
}
