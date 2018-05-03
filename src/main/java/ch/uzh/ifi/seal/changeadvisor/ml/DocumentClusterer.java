package ch.uzh.ifi.seal.changeadvisor.ml;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Topic;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;

import java.util.List;

public interface DocumentClusterer {

    void fit(Corpus corpus, int maxIterations);

    List<TopicAssignment> assignments();

    List<Topic> topics();
}
