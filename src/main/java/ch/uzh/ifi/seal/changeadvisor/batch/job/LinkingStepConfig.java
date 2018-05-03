package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Cluster;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.*;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.labels.LabelFeedbackReader;
import ch.uzh.ifi.seal.changeadvisor.service.LabelService;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElementRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LinkingStepConfig {

    private static final String STEP_NAME = "linking";

    private final StepBuilderFactory stepBuilderFactory;

    private final ClusterReader clusterReader;

    private final LabelService labelService;

    private final LinkingResultRepository resultRepository;

    private final Linker linker;

    private final CodeElementRepository codeElementRepository;

    @Autowired
    public LinkingStepConfig(StepBuilderFactory stepBuilderFactory, ClusterReader clusterReader,
                             LabelService labelService, LinkingResultRepository resultRepository,
                             Linker linker, CodeElementRepository codeElementRepository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.clusterReader = clusterReader;
        this.labelService = labelService;
        this.resultRepository = resultRepository;
        this.linker = linker;
        this.codeElementRepository = codeElementRepository;
    }

    public Step bulkClusterLinking(final String googlePlayId) {
        return stepBuilderFactory.get(STEP_NAME)
                .<Cluster, List<LinkingResult>>chunk(1)
                .reader(clusterReader)
                .processor(clusterProcessor(googlePlayId))
                .writer(tfidfClusterWriter(googlePlayId))
                .build();
    }

    public Step clusterLinking(final String googlePlayId) {
        return stepBuilderFactory.get(STEP_NAME)
                .<Cluster, List<LinkingResult>>chunk(1)
                .reader(labelFeedbackReader(googlePlayId))
                .processor(clusterProcessor(googlePlayId))
                .writer(hdpClusterWriter(googlePlayId))
                .build();
    }

    private LabelFeedbackReader labelFeedbackReader(final String googlePlayId) {
        return new LabelFeedbackReader(labelService, googlePlayId);
    }

    private ClusterProcessor clusterProcessor(final String googlePlayId) {
        return new ClusterProcessor(codeElementRepository, linker, googlePlayId);
    }

    private ClusterWriter hdpClusterWriter(final String googlePlayId) {
        return new ClusterWriter(resultRepository, LinkingResult.ClusterType.HDP, googlePlayId);
    }

    private ClusterWriter tfidfClusterWriter(final String googlePlayId) {
        return new ClusterWriter(resultRepository, LinkingResult.ClusterType.TFIDF, googlePlayId);
    }
}
