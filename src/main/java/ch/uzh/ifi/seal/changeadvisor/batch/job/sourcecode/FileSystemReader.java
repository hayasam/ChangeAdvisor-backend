package ch.uzh.ifi.seal.changeadvisor.batch.job.sourcecode;

import ch.uzh.ifi.seal.changeadvisor.source.parser.bean.ClassBean;
import ch.uzh.ifi.seal.changeadvisor.source.parser.bean.PackageBean;
import org.springframework.batch.item.ItemReader;

public interface FileSystemReader extends ItemReader<ClassBean> {

    /**
     * Sets whether this reviewReader should read the classes sorted or unsorted.
     * Sort order is defined by the {@link PackageBean}.
     *
     * @param sortedRead true iff reads should happens in order. False otherwise.
     * @see PackageBean#compareTo(PackageBean)
     */
    void setSortedRead(boolean sortedRead);

    /**
     * Path to the directory to read.
     *
     * @param path path to directory.
     */
    void setProjectRootPath(String path);
}
