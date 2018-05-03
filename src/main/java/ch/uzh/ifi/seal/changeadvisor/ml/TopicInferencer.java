package ch.uzh.ifi.seal.changeadvisor.ml;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Topic;

import java.util.List;

public interface TopicInferencer {

    List<Topic> topics();
}
