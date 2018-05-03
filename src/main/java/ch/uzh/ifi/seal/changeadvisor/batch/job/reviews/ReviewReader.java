package ch.uzh.ifi.seal.changeadvisor.batch.job.reviews;

import ch.uzh.ifi.seal.changeadvisor.service.ArdocService;
import org.springframework.batch.item.ItemReader;

import java.util.Iterator;
import java.util.List;

public class ReviewReader implements ItemReader<Review> {

    private final String appName;

    private final ArdocService service;

    private Iterator<Review> contentIterator;

    public ReviewReader(ArdocService service, String app) {
        this.appName = app;
        this.service = service;
    }

    @Override
    public Review read() throws Exception {
        return readNext();
    }

    private Review readNext() {
        if (isNotYetInitialized()) {
            List<Review> reviewsSinceLastAnalyzed = service.getReviewsSinceLastAnalyzed(appName);
            contentIterator = reviewsSinceLastAnalyzed.iterator();
        }

        if (!contentIterator.hasNext()) {
            contentIterator = null;
            return null;
        }

        return contentIterator.next();
    }

    private boolean isNotYetInitialized() {
        return contentIterator == null;
    }
}
