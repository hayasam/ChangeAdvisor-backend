package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Writes ArdocResults to DB.
 * Flattens the result set to ArdocResult.
 * Created by alex on 20.07.2017.
 */
@Component
public class ArdocResultsWriter implements ItemWriter<ArdocResults> {

    public static final String COLLECTION_NAME = "ardoc";

    private MongoItemWriter<ArdocResult> writer = new MongoItemWriter<>();

    @Autowired
    public ArdocResultsWriter(MongoTemplate mongoTemplate) {
        writer.setTemplate(mongoTemplate);
        writer.setCollection(COLLECTION_NAME);
    }

    @Override
    public void write(List<? extends ArdocResults> items) throws Exception {
        for (ArdocResults results : items) {
            writer.write(results.getResults());
        }
    }
}
