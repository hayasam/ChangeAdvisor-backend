package ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.preprocessing.CorpusProcessor;
import org.springframework.batch.item.ItemProcessor;

import java.util.Collection;

/**
 * Created by alex on 20.07.2017.
 */
public class FeedbackProcessor implements ItemProcessor<ArdocResult, TransformedFeedback> {

    private CorpusProcessor corpusProcessor;

    private final int threshold;

    public FeedbackProcessor(CorpusProcessor corpusProcessor, int threshold) {
        this.corpusProcessor = corpusProcessor;
        this.threshold = threshold;
    }

    @Override
    public TransformedFeedback process(ArdocResult item) throws Exception {
        Collection<String> bag = corpusProcessor.process(item.getSentence());
        if (bag.size() < threshold) {
            return null;
        }
        return new TransformedFeedback(item, bag);
    }
}
