package ch.uzh.ifi.seal.changeadvisor.source.parser.visitor;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Visitor for methods belonging to a Compilation Unit.
 * <p>
 * Should never be reused after visiting a root node. If needed construct a new visitor.
 * See Visitor pattern.
 * Created by alexanderhofmann on 14.07.17.
 */
public class MethodVisitor extends VoidVisitorAdapter<Void> {

    private StringBuilder sb = new StringBuilder();

    private MethodVisitor() {
        /*
        Avoids instantiation. Use static method instead.
         */
    }

    /**
     * Returns the public corpus for a given {@link ClassOrInterfaceDeclaration}.
     *
     * @param node Compilation unit to parse.
     * @return the public corpus.
     */
    public static String getCorpus(ClassOrInterfaceDeclaration node) {
        MethodVisitor visitor = new MethodVisitor();
        visitor.visit(node, null);
        return visitor.getPublicCorpus();
    }

    /**
     * Visit each method and adds to the string builder only those which are public.
     * Should never be used multiple times. Prefer to create new instance or use the
     * static method {@link #getCorpus(ClassOrInterfaceDeclaration)}}
     *
     * @param n   method declaration node.
     * @param arg args. Unused, void.
     */
    @Override
    public void visit(MethodDeclaration n, Void arg) {
        if (n.isPublic()) {
            String methodText = n.toString();
            sb.append(String.format("%s%n------%n", methodText));
        }
        super.visit(n, arg);
    }

    private String getPublicCorpus() {
        return sb.toString();
    }
}

