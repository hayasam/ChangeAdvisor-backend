package ch.uzh.ifi.seal.changeadvisor.source.parser.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alexanderhofmann on 13.07.17.
 */
public class PackageBean implements Comparable<PackageBean> {

    private final String name;

    private List<CompilationUnitBean> compilationUnits;

    public PackageBean(String name) {
        this.name = name;
        this.compilationUnits = new ArrayList<>();

    }

    public List<CompilationUnitBean> getCompilationUnits() {
        return compilationUnits;
    }

    public void addCompilationUnit(CompilationUnitBean compilationUnit) {
        compilationUnits.add(compilationUnit);
    }

    public String getName() {
        return name;
    }

    public List<ClassBean> getClasses() {
        return compilationUnits.stream().flatMap(c -> c.getClasses().stream()).collect(Collectors.toList());
    }

    public Iterator<ClassBean> classIterator() {
        return getClasses().iterator();
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Compares two package beans.
     * Note: this class has a natural ordering that is inconsistent with equals.
     *
     * @param o other package.
     * @return lexicographical comparison of package names.
     * @see String#compareTo(String)
     */
    @Override
    public int compareTo(PackageBean o) {
        return name.compareTo(o.getName());
    }
}
