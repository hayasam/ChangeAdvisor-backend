package ch.uzh.ifi.seal.changeadvisor.batch.job.linking.labels;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Cluster;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkableReview;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.Label;
import ch.uzh.ifi.seal.changeadvisor.service.LabelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LabelFeedbackReader implements ItemReader<Cluster> {

    private static final Logger logger = LoggerFactory.getLogger(LabelFeedbackReader.class);

    private final LabelService labelService;

    private final String googlePlayId;

    private List<Cluster> clusters;

    private Iterator<Cluster> clusterIterator;

    public LabelFeedbackReader(LabelService labelService, String googlePlayId) {
        this.labelService = labelService;
        this.googlePlayId = googlePlayId;
    }

    @Override
    public Cluster read() {
        if (clusters == null || clusterIterator == null) {
            initFeedback();
        }

        if (!clusterIterator.hasNext()) {
            clusters = null;
            clusterIterator = null;
            return null;
        }

        return clusterIterator.next();
    }

    private void initFeedback() {
        List<Label> labels = labelService.labels(googlePlayId);

        clusters = new ArrayList<>();
        for (Label label : labels) {
            List<? extends LinkableReview> feedbackCorrespondingToLabel = labelService.getFeedbackCorrespondingToLabel(label.getToken(), label.getAppName(), label.getCategory());
            Cluster c = new Cluster(feedbackCorrespondingToLabel);
            clusters.add(c);
        }

        logger.info(String.format("Running linker for %d tfidf clusters.", clusters.size()));
        clusterIterator = clusters.iterator();
    }
}
