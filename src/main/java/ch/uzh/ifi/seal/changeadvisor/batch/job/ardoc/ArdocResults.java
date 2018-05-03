package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import org.ardoc.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the results of an entire review from the Ardoc parser timestamped with its creation time.
 * A review may contain multiple sentences, hence multiple Ardoc results.
 * Created by alex on 17.07.2017.
 */
@SuppressWarnings("unused")
public class ArdocResults implements Iterable<ArdocResult> {

    private List<ArdocResult> results;

    public ArdocResults(Review review, List<Result> results) {
        this.results = results.stream().map(result -> new ArdocResult(review, result)).collect(Collectors.toList());
    }

    public List<ArdocResult> getResults() {
        return results;
    }

    @NotNull
    @Override
    public Iterator<ArdocResult> iterator() {
        return results.iterator();
    }
}
