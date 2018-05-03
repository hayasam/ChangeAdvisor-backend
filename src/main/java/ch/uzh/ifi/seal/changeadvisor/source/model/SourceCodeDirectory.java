package ch.uzh.ifi.seal.changeadvisor.source.model;

public class SourceCodeDirectory {

    private String projectName;

    private String path;

    private String remoteUrl;

    public SourceCodeDirectory(String projectName, String path, String remoteUrl) {
        this.projectName = projectName;
        this.path = path;
        this.remoteUrl = remoteUrl;
    }

    public SourceCodeDirectory(String projectName, String path) {
        this.projectName = projectName;
        this.path = path;
        this.remoteUrl = "";
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    @Override
    public String toString() {
        return "SourceCodeDirectory{" +
                "projectName='" + projectName + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceCodeDirectory that = (SourceCodeDirectory) o;

        if (projectName != null ? !projectName.equals(that.projectName) : that.projectName != null) return false;
        return path != null ? path.equals(that.path) : that.path == null;
    }

    @Override
    public int hashCode() {
        int result = projectName != null ? projectName.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}
