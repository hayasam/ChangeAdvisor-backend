package ch.uzh.ifi.seal.changeadvisor;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignmentRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.Label;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.LabelRepository;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElementRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MongoTestConfig.class})
@ActiveProfiles("test")
public class ChangeadvisorApplicationTests {

    @Autowired
    private CodeElementRepository codeElementRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicAssignmentRepository topicAssignmentRepository;

    @Autowired
    private TransformedFeedbackRepository transformedFeedbackRepository;

    @Autowired
    private ArdocResultRepository ardocResultRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Before
    public void setUp() throws Exception {
        clearDb();
        Label t = new Label("com.test.demo", "FEATURE REQUEST", "bla", 0.99);
        Label label = labelRepository.save(t);
    }

    @After
    public void tearDown() throws Exception {
        clearDb();
    }

    private void clearDb() {
    }

    @Test
    public void contextLoads() {
    }
}
