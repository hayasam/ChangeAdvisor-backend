package ch.uzh.ifi.seal.changeadvisor.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SourceCodeDirectoryDto {

    private static final String FILE_PREFIX = "file://";

    @NotNull(message = "Path may not be null")
    @Size(min = 1, message = "Path may not be empty")
    @Pattern(regexp = "((https://|git://).*.git)|(file://.*)", message = "Path doesn't match any known patterns. Known patterns are: https://*.git or file://*")
    private String path;

    private String projectName;

    private String username;

    private String password;

    public SourceCodeDirectoryDto(
            @JsonProperty(value = "path", required = true) String path,
            @JsonProperty(value = "projectName", required = false) String projectName) {
        this.path = path;
        this.projectName = projectName;
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

    public Path asPath() {
        if (isFileSystemPath()) {
            return Paths.get(path.split(FILE_PREFIX)[1]);
        }
        return Paths.get(path);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isFileSystemPath() {
        return path.startsWith("file://");
    }

    public boolean isGitPath() {
        return (path.startsWith("git://") || path.startsWith("http")) && path.endsWith(".git");
    }
}
