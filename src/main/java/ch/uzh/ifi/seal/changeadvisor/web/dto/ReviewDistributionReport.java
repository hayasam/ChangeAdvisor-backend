package ch.uzh.ifi.seal.changeadvisor.web.dto;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Set;

public class ReviewDistributionReport implements Iterable<ReviewCategoryReport> {

    private Set<ReviewCategoryReport> distribution;

    public ReviewDistributionReport(Iterable<? extends ReviewCategoryReport> distribution) {
        this.distribution = ImmutableSet.copyOf(distribution);
    }

    @SuppressWarnings("unused")
    public int getTotalReviewCount() {
        return distribution.stream().mapToInt(ReviewCategoryReport::getReviewCount).sum();
    }

    public Set<ReviewCategoryReport> getDistribution() {
        return distribution;
    }

    public boolean hasCategory(final String category) {
        for (ReviewCategoryReport reviewCategory : this) {
            if (reviewCategory.getCategory().equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }

    public ReviewCategoryReport findForCategory(final String category) {
        for (ReviewCategoryReport reviewCategory : this) {
            if (reviewCategory.getCategory().equalsIgnoreCase(category)) {
                return reviewCategory;
            }
        }
        throw new IllegalArgumentException(String.format("Found no category %s", category));
    }

    @NotNull
    @Override
    public Iterator<ReviewCategoryReport> iterator() {
        return distribution.iterator();
    }

}
