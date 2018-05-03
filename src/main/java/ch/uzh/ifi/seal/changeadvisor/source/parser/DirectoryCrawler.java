package ch.uzh.ifi.seal.changeadvisor.source.parser;

import ch.uzh.ifi.seal.changeadvisor.batch.job.sourcecode.SourceCodeProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Crawls through directories in order to find all java files.
 * Created by alexanderhofmann on 13.07.17.
 */
class DirectoryCrawler {

    private static final Logger logger = LoggerFactory.getLogger(SourceCodeProcessor.class);

    private static final String JAVA_EXTENSION = ".java";

    private CrawlerFilter filter = file -> file.toString().contains(JAVA_EXTENSION);

    List<Path> explore(Path root) {
        List<Path> pathsExplored = new ArrayList<>();
        explore(root, 0, pathsExplored);
        return pathsExplored;
    }

    private void explore(Path file, int depth, List<Path> paths) {
        if (isDirectory(file)) {
            exploreDirectory(file, depth, paths);
        } else if (filter.filter(file)) {
            paths.add(file);
        }
    }

    private void exploreDirectory(Path directory, int depth, List<Path> paths) {
        try (Stream<Path> stream = Files.list(directory)) {
            stream.forEach(filePath -> explore(filePath, depth + 1, paths));
        } catch (IOException e) {
            logger.error("IOException while parsing directory: " + directory.getFileName(), e);
        }
    }

    private boolean isDirectory(Path file) {
        return Files.isDirectory(file);
    }
}
