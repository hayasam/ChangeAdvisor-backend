package ch.uzh.ifi.seal.changeadvisor.source.importer;

import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;

public class FSSourceImporterTest {

    private static final String IMPORTED_CODE_FOLDER = "imported_code";

    private static final String PROJECT_NAME = "test";

    @Test
    public void importSource() throws Exception {
        SourceCodeDirectoryDto dto = new SourceCodeDirectoryDto(IMPORTED_CODE_FOLDER, PROJECT_NAME);
        FSSourceCodeImporter importer = new FSSourceCodeImporter(dto);
        SourceCodeDirectory directory = importer.importSource();

        Assert.assertThat(directory.getProjectName(), is(PROJECT_NAME));
        Assert.assertThat(directory.getPath(), is(Paths.get(IMPORTED_CODE_FOLDER).toAbsolutePath().toString()));
    }

    @Test
    public void importSourceDefaultProjectName() throws Exception {
        SourceCodeDirectoryDto dto = new SourceCodeDirectoryDto(IMPORTED_CODE_FOLDER, "");
        FSSourceCodeImporter importer = new FSSourceCodeImporter(dto);
        SourceCodeDirectory directory = importer.importSource();

        Assert.assertThat(directory.getProjectName(), is(IMPORTED_CODE_FOLDER));
        Assert.assertThat(directory.getPath(), is(Paths.get(IMPORTED_CODE_FOLDER).toAbsolutePath().toString()));
    }
}