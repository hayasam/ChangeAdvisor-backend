package ch.uzh.ifi.seal.changeadvisor.source;

import ch.uzh.ifi.seal.changeadvisor.source.importer.FSSourceCodeImporter;
import ch.uzh.ifi.seal.changeadvisor.source.importer.GitSourceCodeImporter;
import ch.uzh.ifi.seal.changeadvisor.source.importer.SourceCodeImporter;
import ch.uzh.ifi.seal.changeadvisor.source.importer.SourceCodeImporterFactory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;

public class SourceCodeImporterFactoryTest {

    @Test
    public void getImporterFS() throws Exception {
        final String fileSystemPath = "file://Users/hoal/Document";
        final SourceCodeDirectoryDto dto = new SourceCodeDirectoryDto(fileSystemPath, "");
        SourceCodeImporter importer = SourceCodeImporterFactory.getImporter(dto);
        Assert.assertThat(importer, is(instanceOf(FSSourceCodeImporter.class)));
        Assert.assertThat(importer, not(instanceOf(GitSourceCodeImporter.class)));
    }

    @Test
    public void getImporterGit() throws Exception {
        final String gitPath = "git://https://github.com/a-a-hofmann/SoftwareProject.git";
        final SourceCodeDirectoryDto dto = new SourceCodeDirectoryDto(gitPath, "");
        SourceCodeImporter importer = SourceCodeImporterFactory.getImporter(dto);
        Assert.assertThat(importer, not(instanceOf(FSSourceCodeImporter.class)));
        Assert.assertThat(importer, is(instanceOf(GitSourceCodeImporter.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getImporterEmpty() throws Exception {
        SourceCodeImporterFactory.getImporter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getImporterWrong2() throws Exception {
        SourceCodeImporterFactory.getImporter(new SourceCodeDirectoryDto("adfdsf", "adsf"));
    }
}