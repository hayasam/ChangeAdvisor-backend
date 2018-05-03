package ch.uzh.ifi.seal.changeadvisor.source.parser;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;

/**
 * Created by alexanderhofmann on 13.07.17.
 */
public class DirectoryCrawlerTest {

    private static final String TEST_DIRECTORY = "test_files_parser/com.frostwire.android";

    @Test
    public void exploreDirectoryOneFile() throws Exception {
        DirectoryCrawler explorer = new DirectoryCrawler();

        final String filename = "Hello.java";
        Path root = directoryWithOneFile(filename);

        List<Path> files = explorer.explore(root);
        Assert.assertThat(files.size(), is(1));
        Path file = files.get(0);
        Assert.assertThat(file.getFileName().toString(), is(filename));
    }

    private Path directoryWithOneFile(final String filename) throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path path = fs.getPath("/foo/" + filename);
        Files.createDirectories(path.getParent());
        Files.createFile(path);
        return fs.getPath("/foo");
    }


    @Test
    public void explorNestedDirectoriesOneFile() throws Exception {
        DirectoryCrawler explorer = new DirectoryCrawler();

        final String filename = "Hello.java";
        Path root = nestedDirectoriesWithOneFile(filename);

        List<Path> files = explorer.explore(root);
        Assert.assertThat(files.size(), is(1));
        Path file = files.get(0);
        Assert.assertThat(file.getFileName().toString(), is(filename));
    }

    private Path nestedDirectoriesWithOneFile(final String filename) throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path path = fs.getPath("/foo/bar/baz/" + filename);
        Files.createDirectories(path.getParent());
        Files.createFile(path);
        return fs.getPath("/foo");
    }

    @Test
    public void exploreEmptyDirectory() throws Exception {
        Path root = emptyDirectory();
        DirectoryCrawler explorer = new DirectoryCrawler();

        List<Path> files = explorer.explore(root);
        Assert.assertTrue(files.isEmpty());
    }

    private Path emptyDirectory() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path emptyDirectory = fs.getPath("/foo");
        return Files.createDirectory(emptyDirectory);
    }

    @Test
    public void exploreEmptyDirectories() throws Exception {
        DirectoryCrawler explorer = new DirectoryCrawler();
        Path root = nestedEmptyDirectories();
        List<Path> files = explorer.explore(root);
        Assert.assertTrue(files.isEmpty());
    }

    private Path nestedEmptyDirectories() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path emptyDirectories = fs.getPath("/foo/bar/baz");
        return Files.createDirectories(emptyDirectories);
    }

    @Test
    public void exploreDirectoryMultipleFiles() throws IOException {
        DirectoryCrawler explorer = new DirectoryCrawler();
        String[] filenames = {"Bar.java", "Baz.java", "Foo.java"};
        Path root = directoryWithMutipleFiles(filenames);

        List<Path> files = explorer.explore(root);
        Assert.assertThat(files.size(), is(3));

        for (int i = 0; i < files.size(); i++) {
            Path file = files.get(i);
            Assert.assertThat(file.getFileName().toString(), is(filenames[i]));
        }
    }

    private Path directoryWithMutipleFiles(String... filenames) throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path root = fs.getPath("/foo");
        Files.createDirectories(root);
        for (String filename : filenames) {
            Path path = fs.getPath("/foo/" + filename);
            Files.createFile(path);
        }
        return root;
    }

    @Test
    public void exploreNestedDirectoryWithMultipleFiles() throws Exception {
        DirectoryCrawler explorer = new DirectoryCrawler();
        String[] filenames = {"Bar.java", "Baz.java", "Foo.java", "Alice.java", "Bob.java"};
        Path root = nestedDirectoriesMultipleFiles(filenames[0], filenames[1], filenames[2], filenames[3], filenames[4]);

        List<Path> files = explorer.explore(root);
        List<String> filenamesFromExplorer = files.stream().map(p -> p.getFileName().toString()).collect(Collectors.toList());
        Assert.assertThat(files.size(), is(5));

        for (String filename : filenames) {
            Assert.assertTrue(filenamesFromExplorer.contains(filename));
        }
    }

    private Path nestedDirectoriesMultipleFiles(String f1, String f2, String f3, String f4, String f5) throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path root = fs.getPath("/foo/bar/baz");
        Files.createDirectories(root);

        Path path = fs.getPath("/foo/" + f1);
        Files.createFile(path);
        path = fs.getPath("/foo/bar/" + f2);
        Files.createFile(path);
        path = fs.getPath("/foo/bar/baz/" + f3);
        Files.createFile(path);
        path = fs.getPath("/foo/bar/baz/" + f4);
        Files.createFile(path);
        path = fs.getPath("/foo/bar/baz/" + f5);
        Files.createFile(path);

        return root.getParent().getParent();
    }

    @Test
    public void frostwireProjectTest() throws Exception {
        DirectoryCrawler explorer = new DirectoryCrawler();
        Path frostwireRoot = Paths.get(TEST_DIRECTORY);
        List<Path> paths = explorer.explore(frostwireRoot);
        Assert.assertThat(paths.size(), is(1518));
    }
}
