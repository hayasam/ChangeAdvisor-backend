package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Cluster;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicClusteringResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.*;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.labels.LabelFeedbackReader;
import ch.uzh.ifi.seal.changeadvisor.service.LabelService;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElementRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LinkingStepConfig {

    private static final String STEP_NAME = "linking";
    private static final String STEP_NAME_TFIDF = "linking_tfidf_clusters";

    private final StepBuilderFactory stepBuilderFactory;

    private final BulkClusterReader bulkClusterReader;

    private final BulkClusterProcessor bulkClusterProcessor;

    private final ClusterReader clusterReader;

    private final LabelService labelService;

    private final LinkingResultRepository resultRepository;

    private final Linker linker;

    private final CodeElementRepository codeElementRepository;

    @Autowired
    public LinkingStepConfig(StepBuilderFactory stepBuilderFactory, BulkClusterReader bulkClusterReader,
                             BulkClusterProcessor bulkClusterProcessor, ClusterReader clusterReader,
                             LabelService labelService, LinkingResultRepository resultRepository,
                             Linker linker, CodeElementRepository codeElementRepository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.bulkClusterReader = bulkClusterReader;
        this.bulkClusterProcessor = bulkClusterProcessor;
        this.clusterReader = clusterReader;
        this.labelService = labelService;
        this.resultRepository = resultRepository;
        this.linker = linker;
        this.codeElementRepository = codeElementRepository;
    }

    @Bean
    public Step bulkClusterLinking() {
        return stepBuilderFactory.get(STEP_NAME)
                .<TopicClusteringResult, List<LinkingResult>>chunk(1)
                .reader(bulkClusterReader)
                .processor(bulkClusterProcessor)
                .writer(new ClusterWriter(resultRepository, LinkingResult.ClusterType.HDP, null))
                .build();
    }

    public Step clusterLinking(final String googlePlayId) {
        return stepBuilderFactory.get(STEP_NAME)
                .<Cluster, List<LinkingResult>>chunk(1)
                .reader(labelFeedbackReader(googlePlayId))
                .processor(clusterProcessor(googlePlayId))
                .writer(new ClusterWriter(resultRepository, LinkingResult.ClusterType.TFIDF, googlePlayId))
                .build();
    }

    private ClusterProcessor clusterProcessor(final String googlePlayId) {
        return new ClusterProcessor(codeElementRepository, linker, googlePlayId);
    }

    private LabelFeedbackReader labelFeedbackReader(final String googlePlayId) {
        return new LabelFeedbackReader(labelService, googlePlayId);
    }
}
