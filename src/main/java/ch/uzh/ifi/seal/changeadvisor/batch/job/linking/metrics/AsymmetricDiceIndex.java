package ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics;

import com.google.common.base.Splitter;

import java.util.Collection;
import java.util.List;

public final class AsymmetricDiceIndex implements SimilarityMetric {

    private final MetricUtils metricUtils = new MetricUtils();

    @Override
    public double similarity(String document1, String document2) {
        List<String> docTokens1 = Splitter.on(" ").splitToList(document1);
        List<String> docTokens2 = Splitter.on(" ").splitToList(document2);
        return similarity(docTokens1, docTokens2);
    }

    @Override
    public double similarity(Collection<String> document1, Collection<String> document2) {
        if (document1.isEmpty() || document2.isEmpty()) {
            return 0.0;
        }

        Double overlap = (double) metricUtils.countOverlappingWords(document1, document2);
        Double result = 2 * overlap;

        if (document1.size() < document2.size()) {
            result = result / document1.size();
        } else {
            result = result / document2.size();
        }

        if (result > 1.0) {
            result = 1.0;
        }

        if (result.toString().contains("E")) {
            result = 0.0;
        }

        return result;
    }
}
