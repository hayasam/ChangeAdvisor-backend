package ch.uzh.ifi.seal.changeadvisor.ml;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Topic;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;

import java.util.List;

public class DocumentClustererAdapter implements DocumentClusterer {

    private HierarchicalDirichletProcess hdplda;

    public DocumentClustererAdapter() {
        hdplda = new HierarchicalDirichletProcess(1.0, 0.5, 1.0);
    }

    @Override
    public void fit(Corpus corpus, int maxIterations) {
        hdplda.fit(corpus, maxIterations);
    }

    @Override
    public List<TopicAssignment> assignments() {
        return hdplda.assignments();
    }

    @Override
    public List<Topic> topics() {
        return hdplda.topics();
    }
}
