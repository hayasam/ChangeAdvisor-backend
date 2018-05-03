package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.ml.util.MockResult;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.opencsv.CSVReader;
import org.ardoc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reads trasformed feedback from the file system.
 * Expects the format of the file to be:
 * sentence,category,transformedSentence
 * Created by alex on 24.07.2017.
 */
public class FlatFileTransformedFeedbackReader implements ItemReader<List<TransformedFeedback>> {

    private static final Logger logger = LoggerFactory.getLogger(FlatFileTransformedFeedbackReader.class);

    private final String filePath;

    private final Set<String> inputCategories;

    private boolean hasRead = false;

    public FlatFileTransformedFeedbackReader(String filePath, Set<String> inputCategories) {
        this.filePath = filePath;
        this.inputCategories = inputCategories;
    }


    @Override
    public List<TransformedFeedback> read() throws Exception {
        if (hasRead) {
            return null;
        }

        List<TransformedFeedback> feedbacks = readFromFile(filePath, inputCategories);
        hasRead = true;
        logger.debug(String.format("Read (%d) feedbacks", feedbacks.size()));
        return feedbacks;
    }

    private List<TransformedFeedback> readFromFile(String filePath, Set<String> inputCategories) {
        try (CSVReader reader = new CSVReader(new FileReader(this.filePath), ',', '"', '\\', 1)) {
            return reader.readAll().stream()
                    .filter(line -> line.length == 3 && inputCategories.contains(line[1]))
                    .map(line -> {
                        Result result = new MockResult(line[0], line[1]);
                        ArdocResult ardocResult = new ArdocResult(null, result);
                        LinkedHashSet<String> tokens = Sets.newLinkedHashSet(Splitter.on(" ").omitEmptyStrings().trimResults().split(line[2]));
                        return new TransformedFeedback(ardocResult, tokens);
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error(String.format("Failed to read csv file @ %s. Returning empty list.", filePath));
            return new ArrayList<>();
        }
    }
}
