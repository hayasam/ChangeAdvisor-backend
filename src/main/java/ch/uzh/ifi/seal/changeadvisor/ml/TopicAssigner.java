package ch.uzh.ifi.seal.changeadvisor.ml;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;

import java.util.List;

public interface TopicAssigner {

    List<TopicAssignment> assignments();
}
