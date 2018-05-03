package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Cluster;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElement;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElementRepository;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

/**
 * Processor for document clusters.
 * It processes one cluster at a time and needs all code element informations before starting.
 */
public class ClusterProcessor implements ItemProcessor<Cluster, List<LinkingResult>> {

    private Linker linker;

    private final CodeElementRepository codeElementRepository;

    private List<CodeElement> codeElements;

    private final String googlePlayId;

    public ClusterProcessor(CodeElementRepository codeElementRepository, Linker linker, String googlePlayId) {
        this.codeElementRepository = codeElementRepository;
        this.linker = linker;
        this.googlePlayId = googlePlayId;
    }

    @Override
    public List<LinkingResult> process(Cluster item) {
        if (codeElements == null || codeElements.isEmpty()) {
            codeElements = codeElementRepository.findByAppName(googlePlayId);
        }
        return linker.link(item.getTopicId(), item.getReviews(), codeElements);
    }
}
