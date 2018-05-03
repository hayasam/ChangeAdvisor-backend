package ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import ch.uzh.ifi.seal.changeadvisor.preprocessing.CorpusProcessor;
import com.google.common.collect.Sets;
import org.ardoc.Result;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

/**
 * Created by alex on 20.07.2017.
 */
public class FeedbackProcessorTest {

    @Test
    public void process() throws Exception {
        Result result = Mockito.mock(Result.class);
        Review review = new Review("app");
        when(result.getSentence()).thenReturn("My only complaint is that I'd like to organize things more, remove or add album pics, rearrange things, etc.");
        ArdocResult ardocResult = new ArdocResult(review, result);

        FeedbackProcessor processor = new FeedbackProcessor(
                new CorpusProcessor.Builder()
                        .escapeSpecialChars()
                        .lowerCase()
                        .withContractionExpander()
                        .singularize()
                        .removeStopWords()
                        .stem()
                        .removeTokensShorterThan(3)
                        .build(), 5);

        TransformedFeedback transformedFeedback = processor.process(ardocResult);

        final Set<String> pocResults = Sets.newHashSet("add rearrang complaint etc organ remov".split(" "));
        final Set<String> expectedResults = Sets.newHashSet("add rearrang complaint album etc organ remov pic".split(" "));
        final Collection<String> results = transformedFeedback.getBagOfWords();

        List<String> expectedResultsSorted = new ArrayList<>(expectedResults);
        List<String> resultsSorted = new ArrayList<>(results);
        Collections.sort(expectedResultsSorted);
        Collections.sort(resultsSorted);

        Assert.assertThat(expectedResults.size(), is(results.size()));
        for (String s : results) {
            Assert.assertTrue(expectedResults.contains(s));
        }

        System.out.println(transformedFeedback.getBagOfWords());
    }
}
