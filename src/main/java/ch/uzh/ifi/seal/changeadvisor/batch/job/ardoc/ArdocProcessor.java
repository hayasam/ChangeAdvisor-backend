package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import org.ardoc.Parser;
import org.ardoc.UnknownCombinationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;

/**
 * Processor for app reviews.
 * Created by alex on 17.07.2017.
 */
public class ArdocProcessor implements ItemProcessor<String, ArdocResults> {

    private static final Logger logger = LoggerFactory.getLogger(ArdocProcessor.class);

    private static final Parser parser = Parser.getInstance();

    private static final String ARDOC_METHODS = "NLP+SA";

    private int counter = 0;

    private ExecutionContext executionContext;

    @Override
    public ArdocResults process(String item) throws UnknownCombinationException {
        ArdocResults result = new ArdocResults(null, parser.extract(ARDOC_METHODS, item));
        trackProgress();
        return result;
    }

    private void trackProgress() {
        counter += 1;
        if (counter % 10 == 0) {
            String progressMessage = String.format("Ardoc: Finished processing %d lines.", counter);
            logger.info(progressMessage);
            writeIntoExecutionContext(progressMessage);
        }
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        executionContext = stepExecution.getExecutionContext();
    }

    private <T> void writeIntoExecutionContext(T progress) {
        executionContext.put("ardoc.progress", progress);
    }
}
