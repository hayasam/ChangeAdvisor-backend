package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import java.util.Set;

public interface LinkableReview {

    Set<String> getBag();

    String getOriginalSentence();
}
