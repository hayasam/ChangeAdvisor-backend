package ch.uzh.ifi.seal.changeadvisor.source.parser.visitor;

import ch.uzh.ifi.seal.changeadvisor.source.parser.bean.ClassBean;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;

/**
 * Visitor for methods belonging to a Compilation Unit.
 * <p>
 * Should never be reused after visiting a root node. If needed construct a new visitor.
 * See Visitor pattern.
 * Created by alexanderhofmann on 14.07.17.
 */
public class ClassVisitor extends VoidVisitorAdapter<String> {

    private List<ClassBean> classes = new LinkedList<>();

    private ClassVisitor() {
        /*
        Avoids instantiation. Use static method instead.
         */
    }

    /**
     * Returns the public corpus for a given {@link CompilationUnit}.
     *
     * @param node Compilation unit to parse.
     * @return the public corpus.
     */
    public static List<ClassBean> getClasses(String packageName, CompilationUnit node) {
        ClassVisitor visitor = new ClassVisitor();
        visitor.visit(node, packageName);
        return Lists.newArrayList(visitor.classes);
    }

    /**
     * Visit each class and adds it to the classes list.
     * Should never be used multiple times. Prefer to create new instance or use the
     * static method {@link #getClasses(String, CompilationUnit)}}
     *
     * @param n           method declaration node.
     * @param packageName name of the package this class belong to.
     */
    @Override
    public void visit(ClassOrInterfaceDeclaration n, String packageName) {
        if (!n.isInterface()) {
            classes.add(new ClassBean(n.getNameAsString(), packageName, n));
        }
        super.visit(n, packageName);
    }
}

