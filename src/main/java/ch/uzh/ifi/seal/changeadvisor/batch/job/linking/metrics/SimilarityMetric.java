package ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics;

import java.util.Collection;

public interface SimilarityMetric {

    /**
     * Compute similarity between two documents.
     *
     * @param document1 first document.
     * @param document2 second document.
     * @return
     */
    double similarity(String document1, String document2);

    /**
     * Compute similarity between two documents.
     *
     * @param document1 first document as tokens.
     * @param document2 second document tokens.
     * @return
     */
    double similarity(Collection<String> document1, Collection<String> document2);
}
