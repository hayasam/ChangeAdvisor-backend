package ch.uzh.ifi.seal.changeadvisor.source.importer;

import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitSourceCodeImporter implements SourceCodeImporter {

    private static final Logger logger = LoggerFactory.getLogger(GitSourceCodeImporter.class);

    private static final Pattern PROJECT_NAME_GIT_PATTERN = Pattern.compile(".*/(\\w+).git");

    private static final String CLONING_REPOSITORY = "Cloning project %s from %s to %s";

    private static final String NO_CREDENTIALS_OR_NOT_FOUND = "No Credentials provided for repository or no repository found @ %s";

    private static final String FAILED_TO_CLONE = "Failed to clone the repository @ %s.";

    private static final String CLONED_REPOSITORY = "Cloned repository: %s";

    static final File IMPORTED_CODE_FOLDER = new File("imported_code");

    private final String path;

    private String projectName;

    private String projectDirectoryPath;

    private CredentialsProvider credentialsProvider;

    GitSourceCodeImporter(SourceCodeDirectoryDto dto) {
        this.path = dto.getPath();
        this.projectName = dto.getProjectName() != null ? dto.getProjectName() : "";

        final String username = dto.getUsername() != null ? dto.getUsername() : "";
        final String password = dto.getPassword() != null ? dto.getPassword() : "";
        credentialsProvider = new UsernamePasswordCredentialsProvider(username, password);

        this.projectDirectoryPath = String.format("%s/%s", IMPORTED_CODE_FOLDER.getName(), projectName);
    }

    @Override
    public SourceCodeDirectory importSource() {
        final String REMOTE_URL = getURLFromPath();
        final String projectName = StringUtils.isEmpty(this.projectName) ? getProjectNameFromPath() : this.projectName;
        final File projectPath = new File(projectDirectoryPath);

        if (projectPath.exists()) {
            clearDirectory(projectPath);
        }

        logger.info(String.format(CLONING_REPOSITORY, projectName, REMOTE_URL, projectPath.getPath()));
        try (Git result = cloneRepo(REMOTE_URL, projectPath)) {
            logger.info(String.format(CLONED_REPOSITORY, result.getRepository().getDirectory()));
            return new SourceCodeDirectory(projectName, projectPath.getAbsolutePath(), REMOTE_URL);
        } catch (TransportException e) {
            throw new GitCloneException(String.format(NO_CREDENTIALS_OR_NOT_FOUND, REMOTE_URL), e);
        } catch (GitAPIException e) {
            throw new GitCloneException(String.format(FAILED_TO_CLONE, REMOTE_URL), e);
        }
    }

    private void clearDirectory(File path) {
        try {
            FileUtils.deleteDirectory(path);
        } catch (IOException e) {
            logger.error("Failed to delete directory");
            throw new ProjectDirectoryClearException("Failed to delete directory", e);
        }
    }

    private Git cloneRepo(final String REMOTE_URL, File projectPath) throws GitAPIException {
        return Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(projectPath)
                .setCredentialsProvider(credentialsProvider)
                .call();
    }

    String getURLFromPath() {
        return path.replace("git://", "");
    }

    String getProjectNameFromPath() {
        Matcher matcher = PROJECT_NAME_GIT_PATTERN.matcher(path);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("No *.git pattern found");
    }

    public static class GitCloneException extends RuntimeException {
        GitCloneException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ProjectDirectoryClearException extends RuntimeException {
        ProjectDirectoryClearException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
