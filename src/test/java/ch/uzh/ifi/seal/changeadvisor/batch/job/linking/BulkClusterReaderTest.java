package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.ChangeadvisorApplication;
import ch.uzh.ifi.seal.changeadvisor.MongoTestConfig;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.*;
import com.google.common.collect.ImmutableSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ChangeadvisorApplication.class, MongoTestConfig.class},
        properties = {"spring.batch.job.enabled=false"})
@ActiveProfiles("test")
public class BulkClusterReaderTest {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicAssignmentRepository assignmentRepository;

    @Autowired
    private BulkClusterReader linkingReader;

    @Before
    public void setUp() throws Exception {
        topicRepository.deleteAll();
        assignmentRepository.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        topicRepository.deleteAll();
        assignmentRepository.deleteAll();
    }

    @Test
    public void read() throws Exception {
        final int topicSize = 4;
        final int assignmentSize = 20;

        for (int i = 0; i < topicSize; i++) {
            topicRepository.save(new Topic(ImmutableSet.of(), i));
        }

        for (int i = 0; i < assignmentSize; i++) {
            assignmentRepository.save(new TopicAssignment("", ImmutableSet.of(), i % topicSize));
        }


        TopicClusteringResult clusteringResult = linkingReader.read();

        Assert.assertThat(clusteringResult.topicSize(), is(topicSize));
        for (int i = 0; i < topicSize; i++) {
            Assert.assertThat(clusteringResult.getTopics().get(i).getTopic(), is(i));
        }
        Assert.assertThat(clusteringResult.assignmentSize(), is(assignmentSize));
        for (int i = 0; i < assignmentSize; i++) {
            Assert.assertThat(clusteringResult.getAssignments().get(i).getTopic(), is(i % topicSize));
        }
        // Reading multiple times returns null
        Assert.assertNull(linkingReader.read());
        Assert.assertNull(linkingReader.read());
    }

}
