package ch.uzh.ifi.seal.changeadvisor.source.importer;

import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;

public class GitSourceCodeImporterTest {

    @Test
    public void importSource() throws Exception {
        final String repositoryUrl = "git://https://github.com/a-a-hofmann/SoftwareProject.git";
        final SourceCodeDirectoryDto dto = new SourceCodeDirectoryDto(repositoryUrl, "SoftwareProject");
        GitSourceCodeImporter importer = new GitSourceCodeImporter(dto);
        SourceCodeDirectory directory = importer.importSource();
        Path path = Paths.get(directory.getPath());
        Assert.assertTrue(path.toFile().exists());
        Assert.assertTrue(path.toFile().isDirectory());
        Assert.assertThat(path.getFileName().toString(), is("SoftwareProject"));
        Assert.assertThat(path, is(Paths.get(GitSourceCodeImporter.IMPORTED_CODE_FOLDER + "/SoftwareProject").toAbsolutePath()));
    }

    @Test
    public void getURLFromPath() throws Exception {
        final String repositoryUrl = "git://https://github.com/a-a-hofmann/SoftwareProject.git";
        final SourceCodeDirectoryDto dto = new SourceCodeDirectoryDto(repositoryUrl, "SoftwareProject");
        GitSourceCodeImporter importer = new GitSourceCodeImporter(dto);
        String url = importer.getURLFromPath();
        Assert.assertThat(url, is("https://github.com/a-a-hofmann/SoftwareProject.git"));
    }

    @Test
    public void getProjectNameFromPath() throws Exception {
        final String repositoryUrl = "git://https://github.com/a-a-hofmann/SoftwareProject.git";
        final SourceCodeDirectoryDto dto = new SourceCodeDirectoryDto(repositoryUrl, "SoftwareProject");
        GitSourceCodeImporter importer = new GitSourceCodeImporter(dto);
        String projectName = importer.getProjectNameFromPath();
        Assert.assertThat(projectName, is("SoftwareProject"));
    }
}