package ch.uzh.ifi.seal.changeadvisor.source.parser.bean;

import ch.uzh.ifi.seal.changeadvisor.source.parser.visitor.MethodVisitor;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

/**
 * Created by alex on 14.07.2017.
 */
public class ClassBean implements Comparable<ClassBean> {

    private final String className;

    private final String packageName;

    private final String fullyQualifiedClassName;

    private final String publicCorpus;

    public ClassBean(String className, String packageName, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        this.className = className;
        this.packageName = packageName;
        this.fullyQualifiedClassName = packageName + "." + className;
        this.publicCorpus = getPublicCorpus(classOrInterfaceDeclaration);
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getFullyQualifiedClassName() {
        return fullyQualifiedClassName;
    }

    public String getPublicCorpus() {
        return publicCorpus;
    }

    private String getPublicCorpus(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        return MethodVisitor.getCorpus(classOrInterfaceDeclaration);
    }

    /**
     * Compares two class beans.
     * Note: this class has a natural ordering that is inconsistent with equals.
     *
     * @param o other class bean.
     * @return lexicographical comparison of class names.
     * @see String#compareTo(String)
     */
    @Override
    public int compareTo(ClassBean o) {
        return className.compareTo(o.className);
    }

    @Override
    public String toString() {
        return fullyQualifiedClassName;
    }
}
