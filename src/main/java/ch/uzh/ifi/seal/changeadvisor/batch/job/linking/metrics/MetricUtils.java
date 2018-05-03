package ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics;

import org.springframework.util.Assert;

import java.util.Collection;

public class MetricUtils {

    /**
     * Counts number of recurring words in the document.
     * Multiple occurrences of the same word count multiple times.
     * E.g. doc1 = "hello, world"; doc2 = "hello, hello, world"; ->
     * countOverlappingWords(doc1, doc2) == 3
     *
     * @param doc1
     * @param doc2
     * @return
     */
    int countOverlappingWords(Collection<String> doc1, Collection<String> doc2) {
        Assert.notNull(doc1, "Doc 1 cannot be null!");
        Assert.notNull(doc2, "Doc 2 cannot be null!");
        int counter = 0;
        for (String s1 : doc1) {
            for (String s2 : doc2) {
                if (s1.equals(s2)) {
                    counter++;
                }
            }
        }
        return counter;
    }
}
