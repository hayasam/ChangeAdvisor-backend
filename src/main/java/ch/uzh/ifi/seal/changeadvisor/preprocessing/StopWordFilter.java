package ch.uzh.ifi.seal.changeadvisor.preprocessing;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Filter for stop words. Uses dictionary files located at /resources/nlp for filtering.
 * Created by alex on 14.07.2017.
 */
public class StopWordFilter {

    private static final Logger logger = LoggerFactory.getLogger(StopWordFilter.class);

    private static Set<String> stopwords = ImmutableSet.of();

    private static Set<String> programmingStopwords = ImmutableSet.of();

    static {
        try {
            stopwords = ImmutableSet.copyOf(FileUtils.readLines(Paths.get("src/main/resources/nlp/stopwords").toFile(), "utf8"));
            programmingStopwords = ImmutableSet.copyOf(FileUtils.readLines(Paths.get("src/main/resources/nlp/code_stopwords").toFile(), "utf8"));
        } catch (IOException e) {
            logger.info("Failed to read stopwords file! Not going to annotate stopwords!", e);
        }
    }

    public static boolean isNotStopWord(String token) {
        return isNotNormalStopWord(token.toLowerCase()) && isNotProgrammingStopWord(token.toLowerCase());
    }

    private static boolean isNotNormalStopWord(String token) {
        return stopwords.isEmpty() || !stopwords.contains(token);
    }

    private static boolean isNotProgrammingStopWord(String token) {
        return programmingStopwords.isEmpty() || !programmingStopwords.contains(token);
    }
}
