package ch.uzh.ifi.seal.changeadvisor.source.parser;

import ch.uzh.ifi.seal.changeadvisor.source.parser.bean.PackageBean;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * Tests for FSProjectParser using the frostwire app.
 * Created by alexanderhofmann on 14.07.17.
 */
public class FSProjectParserTest {

    private static final String TEST_DIRECTORY = "test_files_parser/com.frostwire.android";

    private FSProjectParser projectParser = new FSProjectParser();

    @Test
    public void parseFrostwireRootParallel() throws Exception {
        final Path projectRoot = Paths.get(TEST_DIRECTORY);
        List<PackageBean> projectPackages = projectParser.parse(projectRoot, true);
        Assert.assertThat(projectPackages.size(), is(135));

        Assert.assertThat(countCompilationUnits(projectPackages), is(1518));
    }

    @Test
    public void parseFrostwireRootSerial() throws Exception {
        final Path projectRoot = Paths.get(TEST_DIRECTORY);
        List<PackageBean> projectPackages = projectParser.parse(projectRoot, false);
        Assert.assertThat(projectPackages.size(), is(135));

        Assert.assertThat(countCompilationUnits(projectPackages), is(1518));
    }

    @Test
    public void parseCommonRoot() throws Exception {
        final Path projectCommonRoot = Paths.get(TEST_DIRECTORY + "/common");
        List<PackageBean> commonPackages = projectParser.parse(projectCommonRoot, true);
        Assert.assertThat(commonPackages.size(), is(32));

        Assert.assertThat(countCompilationUnits(commonPackages), is(363));
    }

    @Test
    public void parseAndroidRoot() throws Exception {
        final Path projectAndroidRoot = Paths.get(TEST_DIRECTORY + "/android");
        List<PackageBean> androidPackages = projectParser.parse(projectAndroidRoot, true);
        Assert.assertThat(androidPackages.size(), is(50));

        Assert.assertThat(countCompilationUnits(androidPackages), is(331));
    }

    @Test
    public void parseAndroidApolloRoot() throws Exception {
        final Path projectAndroidApolloRoot = Paths.get(TEST_DIRECTORY + "/android/apollo");
        List<PackageBean> androidApolloPackages = projectParser.parse(projectAndroidApolloRoot, true);
        Assert.assertThat(androidApolloPackages.size(), is(22));

        Assert.assertThat(countCompilationUnits(androidApolloPackages), is(148));
    }

    @Test
    public void parseAndroidTestsRoot() throws Exception {
        final Path projectAndroidTestsRoot = Paths.get(TEST_DIRECTORY + "/android/tests");
        List<PackageBean> androidTestspackages = projectParser.parse(projectAndroidTestsRoot, true);
        Assert.assertThat(androidTestspackages.size(), is(6));

        Assert.assertThat(countCompilationUnits(androidTestspackages), is(18));
    }

    @Test
    public void parseDesktopRoot() throws Exception {
        final Path projectDesktopRoot = Paths.get(TEST_DIRECTORY + "/desktop");
        List<PackageBean> desktopPackages = projectParser.parse(projectDesktopRoot, true);
        Assert.assertThat(desktopPackages.size(), is(55));

        Assert.assertThat(countCompilationUnits(desktopPackages), is(824));
    }

    @Test
    public void parseAndGetPublicCorpusFrostwireTest() throws Exception {
        final Path root = Paths.get(TEST_DIRECTORY + "/common/src/main/java/com/frostwire/bittorrent");
        List<PackageBean> packages = projectParser.parse(root, true);
        Assert.assertThat(packages.size(), is(1));

        PackageBean packageBean = packages.get(0);
        Assert.assertThat(packageBean.getCompilationUnits().size(), is(11));
    }

    private int countCompilationUnits(Collection<PackageBean> packages) {
        return packages.stream().mapToInt(p -> p.getCompilationUnits().size()).sum();
    }
}
