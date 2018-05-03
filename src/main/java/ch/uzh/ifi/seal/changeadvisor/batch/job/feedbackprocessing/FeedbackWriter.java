package ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

/**
 * Created by alex on 24.07.2017.
 */
public class FeedbackWriter implements ItemWriter<TransformedFeedback> {

    public static final String COLLECTION_NAME = "transformedFeedback";

    private MongoItemWriter<TransformedFeedback> writer;

    public FeedbackWriter(MongoTemplate mongoTemplate) {
        writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection(COLLECTION_NAME);
    }

    @Override
    public void write(List<? extends TransformedFeedback> items) throws Exception {
        writer.write(items);
    }
}
