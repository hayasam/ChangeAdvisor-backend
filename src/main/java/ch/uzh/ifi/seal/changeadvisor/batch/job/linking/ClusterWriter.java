package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Writes results of clustering process.
 * Can write both the results of bulk processing and one-cluster-at-a-time processing.
 */
public class ClusterWriter implements ItemWriter<List<LinkingResult>> {

    private static final Logger logger = LoggerFactory.getLogger(ClusterWriter.class);

    private final LinkingResultRepository repository;

    private final LinkingResult.ClusterType clusterType;

    private final String appName;

    public ClusterWriter(LinkingResultRepository repository, LinkingResult.ClusterType clusterType, @Nullable String appName) {
        this.repository = repository;
        this.clusterType = clusterType;
        this.appName = appName;
    }

    @BeforeStep
    public void deleteAllPreviousResults(StepExecution stepExecution) {
        if (appName != null) {
            logger.info("Deleting previous linking results.");
            long resultsDeleted = repository.deleteByAppName(appName);
            logger.info("Deleted %d results", resultsDeleted);
        }
    }

    @Override
    public void write(List<? extends List<LinkingResult>> items) {
        setClusterTypeAndAppNameOnResults(items);
        items.forEach(repository::saveAll);
    }

    private void setClusterTypeAndAppNameOnResults(List<? extends List<LinkingResult>> items) {
        items.forEach(results -> results.forEach(result -> {
            result.setClusterType(clusterType);
            result.setAppName(appName);
        }));
    }
}
