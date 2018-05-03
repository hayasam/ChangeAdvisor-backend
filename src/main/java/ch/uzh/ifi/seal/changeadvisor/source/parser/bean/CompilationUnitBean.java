package ch.uzh.ifi.seal.changeadvisor.source.parser.bean;

import ch.uzh.ifi.seal.changeadvisor.source.parser.visitor.ClassVisitor;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithName;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Represents a compilation unit (essentialy a file).
 * Created by alexanderhofmann on 14.07.17.
 */
public final class CompilationUnitBean {

    private String packageName;

    private List<ClassBean> classes;

    private CompilationUnitBean(CompilationUnit compilationUnit) {
        this.packageName = getPackageName(compilationUnit);
        this.classes = parseCompilationUnitForClasses(compilationUnit);
    }

    public static CompilationUnitBean fromPath(Path path) throws IOException {
        CompilationUnit cu = JavaParser.parse(path);
        return new CompilationUnitBean(cu);
    }

    public String getPackageName() {
        return packageName;
    }

    private String getPackageName(CompilationUnit compilationUnit) {
        Optional<PackageDeclaration> packageDeclaration = compilationUnit.getPackageDeclaration();
        return packageDeclaration.map(NodeWithName::getNameAsString).orElse("default");
    }

    public List<ClassBean> getClasses() {
        return classes;
    }

    private List<ClassBean> parseCompilationUnitForClasses(CompilationUnit compilationUnit) {
        return ClassVisitor.getClasses(packageName, compilationUnit);
    }
}
