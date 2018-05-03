package ch.uzh.ifi.seal.changeadvisor.batch.job.sourcecode;

import ch.uzh.ifi.seal.changeadvisor.source.parser.FSProjectParser;
import ch.uzh.ifi.seal.changeadvisor.source.parser.bean.ClassBean;
import ch.uzh.ifi.seal.changeadvisor.source.parser.bean.PackageBean;
import org.springframework.util.Assert;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * File System project reviewReader.
 * Created by alex on 15.07.2017.
 */
public class FSProjectReader implements FileSystemReader {

    private final FSProjectParser projectParser;

    private Path projectRootPath;

    private Iterator<PackageBean> packageIterator;

    private Iterator<ClassBean> classIterator;

    private List<PackageBean> packages;

    private boolean isSortedRead;

    public FSProjectReader(FSProjectParser projectParser) {
        this.projectParser = projectParser;
        this.isSortedRead = false;
    }

    @Override
    public void setProjectRootPath(String projectRoot) {
        Assert.notNull(projectRoot, "Project root to parse must not be null.");
        this.projectRootPath = Paths.get(projectRoot);
        Assert.isTrue(projectRootPath.toFile().exists(), String.format("Path [%s] does not exist.", projectRootPath));
        Assert.isTrue(projectRootPath.toFile().isDirectory(),
                String.format("Path [%s] is not a directory. Can't be Project root.", projectRootPath));
    }

    /**
     * Sets whether this reviewReader should read the classes sorted or unsorted.
     * Sort order is defined by the {@link PackageBean}.
     *
     * @param sortedRead true iff reads should happens in order. False otherwise.
     * @see PackageBean#compareTo(PackageBean)
     */
    @Override
    public void setSortedRead(boolean sortedRead) {
        isSortedRead = sortedRead;
    }

    @Override
    public ClassBean read() throws Exception {
        if (hasNotParsedYet()) {
            packages = parse();
            packageIterator = packages.iterator();
        }
        if (needsNewClassIterator()) {
            classIterator = getNextClassIterator();
        }

        ClassBean classBean = classIterator.hasNext() ? classIterator.next() : null;
        if (isDone(classBean)) {
            clearReader();
        }

        return classBean;
    }

    private List<PackageBean> parse() {
        Assert.notNull(projectRootPath, "Must set a path in config before running batch job.");
        List<PackageBean> projectPackages = projectParser.parse(projectRootPath, false);

        if (isSortedRead) {
            Collections.sort(projectPackages);
        }
        return projectPackages;
    }

    private boolean hasNotParsedYet() {
        return packages == null;
    }

    private boolean needsNewClassIterator() {
        return hasNoNextClass() && hasNextPackage();
    }

    private Iterator<ClassBean> getNextClassIterator() {
        return packageIterator.next().classIterator();
    }

    private boolean hasNoNextClass() {
        return classIterator == null || !classIterator.hasNext();
    }

    private boolean hasNextPackage() {
        return packageIterator.hasNext();
    }

    /**
     * If class iterator returns a null, it means there are no more classes to parse.
     * We can free resources by calling {@link #clearReader()}
     *
     * @param classBean last classBean read.
     * @return true iff there are no more classes to read;
     */
    private boolean isDone(ClassBean classBean) {
        return classBean == null;
    }

    /**
     * To avoid holding on to resources once the reader is done, we should clear this reader to free resources.
     */
    private void clearReader() {
        packages.clear();
        packageIterator = packages.iterator();
        classIterator = null;
    }
}
