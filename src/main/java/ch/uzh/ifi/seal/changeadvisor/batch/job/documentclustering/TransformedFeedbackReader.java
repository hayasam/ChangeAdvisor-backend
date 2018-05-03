package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemReader;

import java.util.List;
import java.util.Set;

/**
 * Created by alex on 24.07.2017.
 */
public class TransformedFeedbackReader implements ItemReader<List<TransformedFeedback>> {

    private static final Set<String> ARDOC_CATEGORIES = ImmutableSet.of("FEATURE REQUEST", "PROBLEM DISCOVERY");

    private TransformedFeedbackRepository feedbackRepository;

    private boolean hasRead = false;

    private final String appName;

    public TransformedFeedbackReader(TransformedFeedbackRepository feedbackRepository, String appName) {
        this.feedbackRepository = feedbackRepository;
        this.appName = appName;
    }

    @Override
    public List<TransformedFeedback> read() throws Exception {
        if (hasRead) {
            return null;
        }

        List<TransformedFeedback> feedback;
        if (StringUtils.isEmpty(appName)) {
            feedback = readAll();
        } else {
            feedback = readFeedbackForApp();
        }
        hasRead = true;
        return feedback;
    }

    private List<TransformedFeedback> readAll() {
        return feedbackRepository.findAllByArdocResultCategoryIn(ARDOC_CATEGORIES);
    }

    private List<TransformedFeedback> readFeedbackForApp() {
        return feedbackRepository.findByArdocResultAppNameAndArdocResultCategoryIn(appName, ARDOC_CATEGORIES);
    }
}
