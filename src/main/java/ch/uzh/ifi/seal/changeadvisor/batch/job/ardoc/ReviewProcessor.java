package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import org.ardoc.Parser;
import org.ardoc.Result;
import org.ardoc.UnknownCombinationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

public class ReviewProcessor implements ItemProcessor<Review, ArdocResults> {

    private static final Logger logger = LoggerFactory.getLogger(ReviewProcessor.class);

    private static final Parser parser = Parser.getInstance();

    private static final String ARDOC_METHODS = "NLP+SA";

    private int counter = 0;

    private ExecutionContext executionContext;

    @Override
    public ArdocResults process(Review item) throws UnknownCombinationException {
        List<Result> results = parser.extract(ARDOC_METHODS, item.getReviewText());
        ArdocResults result = new ArdocResults(item, results);
        trackProgress();
        return result;
    }

    private void trackProgress() {
        counter += 1;
        if (counter % 10 == 0) {
            final String progressMessage = String.format("Ardoc: Finished processing %d lines.", counter);
            logger.info(progressMessage);
            writeIntoExecutionContext(progressMessage);
        }
    }

    @SuppressWarnings("unused")
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        executionContext = stepExecution.getExecutionContext();
    }

    private <T> void writeIntoExecutionContext(T progress) {
        executionContext.put("ardoc.progress", progress);
    }
}
