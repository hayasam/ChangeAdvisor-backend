package ch.uzh.ifi.seal.changeadvisor.source.importer;

import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;

import java.nio.file.Path;

public class FSSourceCodeImporter implements SourceCodeImporter {

    private static final String PATH_NOT_FOUND_OR_NOT_DIRECTORY = "Path [%s] doesn't exist or is not directory";

    private final Path path;

    private String projectName;

    FSSourceCodeImporter(SourceCodeDirectoryDto dto) {
        this.path = dto.asPath();
        this.projectName = dto.getProjectName();

        if (org.apache.commons.lang3.StringUtils.isEmpty(this.projectName)) {
            this.projectName = projectNameFromPath(this.path);
        }
    }

    @Override
    public SourceCodeDirectory importSource() {
        validatePath(path);
        return new SourceCodeDirectory(this.projectName, path.toAbsolutePath().toString());
    }

    /**
     * Checks if the path is valid, whether it exists and is a directory.
     * Throws exception in case it isn't valid. Passes silently otherwise.
     *
     * @param path to validate.
     */
    private void validatePath(Path path) {
        if (!path.toFile().exists() || !path.toFile().isDirectory()) {
            throw new IllegalArgumentException(String.format(PATH_NOT_FOUND_OR_NOT_DIRECTORY, path.toString()));
        }
    }

    private String projectNameFromPath(Path path) {
        return path.getFileName().toString();
    }
}
