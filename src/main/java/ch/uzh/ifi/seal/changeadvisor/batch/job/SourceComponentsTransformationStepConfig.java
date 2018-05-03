package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.sourcecode.FSDeferredProjectReader;
import ch.uzh.ifi.seal.changeadvisor.batch.job.sourcecode.FSProjectReader;
import ch.uzh.ifi.seal.changeadvisor.batch.job.sourcecode.SourceCodeProcessor;
import ch.uzh.ifi.seal.changeadvisor.preprocessing.CorpusProcessor;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElement;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElementRepository;
import ch.uzh.ifi.seal.changeadvisor.source.parser.FSProjectParser;
import ch.uzh.ifi.seal.changeadvisor.source.parser.bean.ClassBean;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Configuration of "Extract Bag of words" step of ChangeAdvisor.
 * Created by alex on 17.07.2017.
 */
@Component
public class SourceComponentsTransformationStepConfig {

    private static final String STEP_NAME = "sourceCodeTransformation";

    private static final String TEST_DIRECTORY = "test_files_parser";

    private final StepBuilderFactory stepBuilderFactory;

    private final FSProjectParser projectParser;

    private final MongoTemplate mongoTemplate;

    private final CodeElementRepository codeElementRepository;

    @Autowired
    public SourceComponentsTransformationStepConfig(StepBuilderFactory stepBuilderFactory,
                                                    FSProjectParser projectParser, MongoTemplate mongoTemplate,
                                                    CodeElementRepository codeElementRepository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.projectParser = projectParser;
        this.mongoTemplate = mongoTemplate;
        this.codeElementRepository = codeElementRepository;
    }

    public Step extractBagOfWords(String projectPath) {
        return stepBuilderFactory.get(STEP_NAME)
                .allowStartIfComplete(true)
                .<ClassBean, CodeElement>chunk(10)
                .reader(reader(projectPath))
                .processor(processor())
                .writer(mongoWriter())
                .build();
    }

    public Step extractBagOfWordsDeferredPath() {
        return stepBuilderFactory.get(STEP_NAME)
                .allowStartIfComplete(true)
                .<ClassBean, CodeElement>chunk(100)
                .reader(deferredReader())
                .processor(processor())
                .writer(mongoWriter())
                .build();
    }

    public FSProjectReader reader(String projectPath) {
        FSProjectReader reader = new FSProjectReader(projectParser);
        reader.setProjectRootPath(projectPath);
        reader.setSortedRead(true);
        return reader;
    }

    public FSDeferredProjectReader deferredReader() {
        FSDeferredProjectReader reader = new FSDeferredProjectReader(new FSProjectReader(projectParser));
        reader.setSortedRead(true);
        return reader;
    }

    public SourceCodeProcessor processor() {
        CorpusProcessor corpusProcessor = new CorpusProcessor.Builder()
                .escapeSpecialChars()
                .withComposedIdentifierSplit()
//                .withAutoCorrect(new EnglishSpellChecker()) // Warning huge performance impact!
                .withContractionExpander()
                .singularize()
                .removeStopWords()
                .lowerCase()
                .stem()
                .removeTokensShorterThan(3)
                .build();
        return new SourceCodeProcessor(5, corpusProcessor, codeElementRepository);
    }

    @Bean
    public FlatFileItemWriter<CodeElement> fileWriter() {
        FlatFileItemWriter<CodeElement> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(TEST_DIRECTORY + "/batch_test.csv"));
        writer.setHeaderCallback(headerWriter -> headerWriter.write("component,bag"));
        writer.setLineAggregator(CodeElement::asCsv);
        return writer;
    }

    @Bean
    public ItemWriter<CodeElement> mongoWriter() {
        MongoItemWriter<CodeElement> mongoItemWriter = new MongoItemWriter<>();
        mongoItemWriter.setTemplate(mongoTemplate);
        return mongoItemWriter;
    }
}
