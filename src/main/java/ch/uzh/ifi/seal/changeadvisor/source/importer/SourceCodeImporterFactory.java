package ch.uzh.ifi.seal.changeadvisor.source.importer;

import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.apache.commons.lang3.StringUtils;

public class SourceCodeImporterFactory {

    private static final String FAILED_TO_CREATE_IMPORTER = "Couldn't instantiate Source code importer. Can't determine path for %s";

    /**
     * Gets the appropriate source code importer for the given path.
     * Additionally sets the credentials if provided in the dto.
     *
     * @param dto {@link SourceCodeDirectoryDto}
     * @return Source code importer
     * @see SourceCodeImporter
     * @see FSSourceCodeImporter
     * @see GitSourceCodeImporter
     */
    public static SourceCodeImporter getImporter(SourceCodeDirectoryDto dto) {
        validateDto(dto);

        if (dto.isFileSystemPath()) {
            return new FSSourceCodeImporter(dto);
        }

        if (dto.isGitPath()) {
            return new GitSourceCodeImporter(dto);
        }

        throw new IllegalArgumentException(String.format(FAILED_TO_CREATE_IMPORTER, dto.getPath()));
    }

    /**
     * Checks if the dto is valid, as in it is not null and has not an empty path.
     * Throws exception in case it isn't valid. Passes silently otherwise.
     *
     * @param dto dto to validate.
     */
    private static void validateDto(SourceCodeDirectoryDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Source code directory dto may not be null");
        }
        if (StringUtils.isEmpty(dto.getPath())) {
            throw new IllegalArgumentException("Path to source code may not be empty");
        }
    }
}
