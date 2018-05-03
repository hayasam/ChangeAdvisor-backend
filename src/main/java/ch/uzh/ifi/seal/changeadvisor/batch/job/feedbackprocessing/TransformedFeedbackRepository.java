package ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Created by alex on 24.07.2017.
 */
@Repository
public interface TransformedFeedbackRepository extends MongoRepository<TransformedFeedback, String> {

    List<TransformedFeedback> findTop1ByArdocResultAppNameOrderByTimestampDesc(String appName);

    List<TransformedFeedback> findAllByArdocResultCategoryAndArdocResultAppName(String category, String appName);

    List<TransformedFeedback> findAllByArdocResultAppName(String appName);

    List<TransformedFeedback> findAllByArdocResultCategoryIn(Collection<String> categories);

    List<TransformedFeedback> findByArdocResultAppNameAndArdocResultCategoryIn(String appName, Collection<String> categories);

    List<TransformedFeedback> findByArdocResultAppNameAndTransformedSentenceContainingIgnoreCase(String appName, String label);

    List<TransformedFeedback> findDistinctByArdocResultAppNameAndArdocResultCategoryAndTransformedSentenceContainingIgnoreCase(String appName, String category, String label);


    int deleteByArdocResultAppName(String appNAme);
}
