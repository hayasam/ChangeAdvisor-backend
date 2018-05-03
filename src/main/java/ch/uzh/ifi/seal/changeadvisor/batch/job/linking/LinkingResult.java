package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;

@Document
public class LinkingResult implements Comparable<LinkingResult> {

    public enum ClusterType {
        TFIDF, HDP
    }

    @Id
    private String id;

    private String appName;

    private String clusterId;

    private Collection<String> reviews;

    private Collection<String> clusterBag;

    private Collection<String> codeComponentBag;

    private String codeComponentName;

    private Double similarity;

    private ClusterType clusterType;

    public LinkingResult() {
    }

    public LinkingResult(String clusterId, Collection<String> reviews, Collection<String> clusterBag, Collection<String> codeComponentBag, String codeComponentName, Double similarity, ClusterType clusterType) {
        this.clusterId = clusterId;
        this.reviews = reviews;
        this.clusterBag = clusterBag;
        this.codeComponentBag = codeComponentBag;
        this.codeComponentName = codeComponentName;
        this.similarity = similarity;
        this.clusterType = clusterType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public Collection<String> getReviews() {
        return reviews;
    }

    public void setReviews(Collection<String> reviews) {
        this.reviews = reviews;
    }

    public Collection<String> getClusterBag() {
        return clusterBag;
    }

    public void setClusterBag(Collection<String> clusterBag) {
        this.clusterBag = clusterBag;
    }

    public Collection<String> getCodeComponentBag() {
        return codeComponentBag;
    }

    public void setCodeComponentBag(Collection<String> codeComponentBag) {
        this.codeComponentBag = codeComponentBag;
    }

    public String getCodeComponentName() {
        return codeComponentName;
    }

    public void setCodeComponentName(String codeComponentName) {
        this.codeComponentName = codeComponentName;
    }

    public Double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }

    public ClusterType getClusterType() {
        return clusterType;
    }

    public void setClusterType(ClusterType clusterType) {
        this.clusterType = clusterType;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public int compareTo(@NotNull LinkingResult o) {
        return codeComponentName.compareTo(o.codeComponentName);
    }

    @Override
    public String toString() {
        return "LinkingResult{" +
                "id='" + id + '\'' +
                ", clusterId=" + clusterId +
                ", clusterBag=" + clusterBag +
                ", codeComponentBag=" + codeComponentBag +
                ", codeComponentName='" + codeComponentName + '\'' +
                ", similarity=" + similarity +
                '}';
    }
}
