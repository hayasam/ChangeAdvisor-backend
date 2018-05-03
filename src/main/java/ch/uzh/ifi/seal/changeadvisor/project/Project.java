package ch.uzh.ifi.seal.changeadvisor.project;

import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Date;

@Document
public class Project implements Comparable<Project> {

    @Id
    private String id;

    @Indexed(unique = true)
    private String appName;

    private String googlePlayId;

    private String path;

    private String remoteUrl;

    private String cronSchedule;

    private ReviewsConfig reviewsConfig;

    private SourceConfig sourceConfig;

    public Project() {
    }

    public Project(String appName, String path, String remoteUrl, String cronSchedule) {
        Assert.isTrue(!StringUtils.isEmpty(appName), "App name cannot be empty!");
        Assert.isTrue(checkCronExpression(cronSchedule), String.format("Cron expression is not valid. Got %s.", cronSchedule));
        this.appName = appName;
        this.path = path;
        this.remoteUrl = remoteUrl;
        this.cronSchedule = cronSchedule;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String getCronSchedule() {
        return cronSchedule;
    }

    public boolean hasCronSchedule() {
        return !StringUtils.isEmpty(cronSchedule);
    }

    public void setCronSchedule(String cronSchedule) {
        this.cronSchedule = cronSchedule;
    }

    public boolean hasValidCronExpression() {
        return checkCronExpression(cronSchedule);
    }

    public String getGooglePlayId() {
        return googlePlayId;
    }

    public void setGooglePlayId(String googlePlayId) {
        this.googlePlayId = googlePlayId;
    }

    public void setReviewsConfig(ReviewsConfig reviewsConfig) {
        this.reviewsConfig = reviewsConfig;
    }

    public ReviewsConfig getReviewsConfig() {
        return reviewsConfig;
    }

    public SourceConfig getSourceConfig() {
        return sourceConfig;
    }

    public void setSourceConfig(SourceConfig sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    public void justImportedSource() {
        this.sourceConfig = new SourceConfig(new Date());
    }

    /**
     * A cron expression can either be empty (manual triggering of review import)
     * or it has to be a valid cron expression.
     *
     * @param expression expression to verify.
     * @return true iff expression is valid cron expression.
     */
    private boolean checkCronExpression(String expression) {
        return StringUtils.isEmpty(expression) || CronSequenceGenerator.isValidExpression(expression);
    }

    public void setSourceCodeDirectory(SourceCodeDirectory directory) {
        this.path = directory.getPath();
        this.remoteUrl = directory.getRemoteUrl();
    }

    public SourceCodeDirectory asSourceCodeDirectory() {
        return new SourceCodeDirectory(appName, path);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", appName='" + appName + '\'' +
                ", googlePlayId='" + googlePlayId + '\'' +
                ", path='" + path + '\'' +
                ", remoteUrl='" + remoteUrl + '\'' +
                ", cronSchedule='" + cronSchedule + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (appName != null ? !appName.equals(project.appName) : project.appName != null) return false;
        if (googlePlayId != null ? !googlePlayId.equals(project.googlePlayId) : project.googlePlayId != null)
            return false;
        if (path != null ? !path.equals(project.path) : project.path != null) return false;
        return remoteUrl != null ? remoteUrl.equals(project.remoteUrl) : project.remoteUrl == null;
    }

    @Override
    public int hashCode() {
        int result = appName != null ? appName.hashCode() : 0;
        result = 31 * result + (googlePlayId != null ? googlePlayId.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (remoteUrl != null ? remoteUrl.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(@NotNull Project o) {
        return appName.compareTo(o.appName);
    }
}
