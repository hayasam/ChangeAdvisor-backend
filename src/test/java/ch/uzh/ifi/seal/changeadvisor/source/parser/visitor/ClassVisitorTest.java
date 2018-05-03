package ch.uzh.ifi.seal.changeadvisor.source.parser.visitor;

import ch.uzh.ifi.seal.changeadvisor.source.parser.bean.ClassBean;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * Created by alex on 14.07.2017.
 */
public class ClassVisitorTest {

    private static final String TEST_DIRECTORY = "test_files_parser";
    private static final String TEST_FROSTWIRE_APP = "com.frostwire.android";

    @Test
    public void visitClasses() throws Exception {
        Path pathToCu = Paths.get(TEST_DIRECTORY + "/" + TEST_FROSTWIRE_APP + "/common/src/main/java/com/frostwire/bittorrent/BTDownload.java");
        CompilationUnit cu = JavaParser.parse(pathToCu);

        String packageName = cu.getPackageDeclaration().map(NodeWithName::getNameAsString).orElse("default");
        List<ClassBean> classes = ClassVisitor.getClasses(packageName, cu);
        Assert.assertEquals(1, classes.size());
        Assert.assertThat(classes.get(0).getClassName(), is("BTDownload"));
        Assert.assertThat(classes.get(0).getFullyQualifiedClassName(), is("com.frostwire.bittorrent.BTDownload"));
    }
}