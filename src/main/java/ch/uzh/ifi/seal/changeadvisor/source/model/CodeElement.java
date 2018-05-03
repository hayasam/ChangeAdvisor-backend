package ch.uzh.ifi.seal.changeadvisor.source.model;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Represents a code component. A set of words and the code component they are derived from.
 * Created by alex on 14.07.2017.
 */
@Document
public class CodeElement implements Comparable<CodeElement> {

    @Id
    private String id;

    private String appName;

    private String fullyQualifiedClassName;

    private Collection<String> bag;

    private LocalDateTime timestamp = LocalDateTime.now();

    CodeElement() {
    }

    public CodeElement(String appName, String fullyQualifiedClassName, Collection<String> bag) {
        this.appName = appName;
        this.fullyQualifiedClassName = fullyQualifiedClassName;
        this.bag = bag;
    }

    public String getId() {
        return id;
    }

    public Collection<String> getBag() {
        return bag;
    }

    public String getAppName() {
        return appName;
    }

    /**
     * Returns an immutable sorted copy of this bag.
     *
     * @return immutable sorted bag of words.
     */
    public List<String> getSortedBag() {
        return ImmutableList.sortedCopyOf(bag);
    }

    public String getFullyQualifiedClassName() {
        return fullyQualifiedClassName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int size() {
        return bag.size();
    }

    /**
     * Writes bag of words to file as .csv with the following format:
     * packageName,bagOfWords
     *
     * @param path   path to the file to write
     * @param append if {@code true}, then the data will be added to the
     *               end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     */
    public void writeToFile(Path path, boolean append) throws IOException {
        final String bagString = asCsv();
        FileUtils.write(path.toFile(), fullyQualifiedClassName + "," + bagString, "utf8", append);
    }

    public String asCsv() {
        String bagString = String.join(" ", bag);
        return String.format("%s,%s", fullyQualifiedClassName, bagString);
    }

    @Override
    public String toString() {
        return bag.toString();
    }

    /**
     * Compares two bags.
     * Note: this class has a natural ordering that is inconsistent with equals.
     *
     * @param o other bag.
     * @return lexicographical comparison of FQCN names.
     * @see String#compareTo(String)
     */
    @Override
    public int compareTo(CodeElement o) {
        return fullyQualifiedClassName.compareTo(o.fullyQualifiedClassName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CodeElement that = (CodeElement) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (fullyQualifiedClassName != null ? !fullyQualifiedClassName.equals(that.fullyQualifiedClassName) : that.fullyQualifiedClassName != null)
            return false;
        if (bag != null ? !bag.equals(that.bag) : that.bag != null) return false;
        return timestamp != null ? timestamp.equals(that.timestamp) : that.timestamp == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (fullyQualifiedClassName != null ? fullyQualifiedClassName.hashCode() : 0);
        result = 31 * result + (bag != null ? bag.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}
