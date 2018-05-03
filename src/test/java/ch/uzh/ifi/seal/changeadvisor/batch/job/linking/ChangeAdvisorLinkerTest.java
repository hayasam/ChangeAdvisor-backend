package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Topic;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElement;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;

public class ChangeAdvisorLinkerTest {

    private static final Logger logger = LoggerFactory.getLogger(ChangeAdvisorLinkerTest.class);

    private ChangeAdvisorLinker linker = new ChangeAdvisorLinker();

    private static final int TOPIC_SIZE = 4;
    private static final int ASSIGNMENT_SIZE = 20;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void processIteratively() throws Exception {
        List<TopicAssignment> assignments = readAssignments(Paths.get("test_files_parser/linking/com.frostwire.android_assignments.csv"));
        List<CodeElement> codeElements = readSourceComponents(Paths.get("test_files_parser/linking/source_components_frostwire.csv"));

        Assert.assertThat(assignments.size(), is(1375));
        Assert.assertThat(codeElements.size(), is(1359));

        logger.info("Finished reading, starting linking.");

        List<LinkingResult> results = new ArrayList<>(300);

        Map<Integer, List<TopicAssignment>> clusters = linker.groupByTopic(assignments);

        for (Map.Entry<Integer, List<TopicAssignment>> entry : clusters.entrySet()) {
            List<LinkingResult> clusterResults = linker.link(entry.getKey().toString(), entry.getValue(), codeElements);
            results.addAll(clusterResults);
        }

        logger.info(String.format("Results found: %d.", results.size()));
        Assert.assertThat(results.size(), is(911)); // Results should be in the ~300 range.
    }

    @SuppressWarnings("unused")
    private void writeResultsToCsv(List<LinkingResult> results) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(new File("test_files_parser/linking/output.csv")));
        Collections.sort(results);
        for (LinkingResult result : results) {
            writer.writeNext(new String[]{
                    result.getClusterId(),
                    Joiner.on(" ").join(result.getClusterBag()),
                    result.getCodeComponentName(),
                    result.getSimilarity().toString(),
                    Joiner.on(" ").join(result.getCodeComponentBag())
            });
        }
    }

    private List<CodeElement> readSourceComponents(Path path) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(path.toFile()));
        String[] line = reader.readNext();

        List<CodeElement> elements = new ArrayList<>();
        while ((line = reader.readNext()) != null) {
            if (!line[2].isEmpty()) {
                elements.add(new CodeElement("", line[0], Sets.newHashSet(line[2].split(" "))));
            }
        }
        return elements;
    }

    private List<TopicAssignment> readAssignments(Path path) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(path.toFile()));
        String[] line = reader.readNext();

        List<TopicAssignment> assignments = new ArrayList<>();
        while ((line = reader.readNext()) != null) {
            assignments.add(new TopicAssignment(Sets.newHashSet(line[1].split(" ")), Integer.valueOf(line[2])));
        }
        return assignments;
    }

    @Test
    public void groupBy() throws Exception {
        List<Topic> topics = createTopics(TOPIC_SIZE);
        List<TopicAssignment> assignments = createAssignments(ASSIGNMENT_SIZE);
        Map<Integer, List<TopicAssignment>> map = linker.groupByTopic(assignments);

        Assert.assertThat(map.size(), is(topics.size()));
        topics.forEach(t -> Assert.assertTrue(map.containsKey(t.getTopicId())));
        assignments.forEach(a -> Assert.assertTrue(map.get(a.getTopic()).contains(a)));


        Map<Integer, List<TopicAssignment>> map2 = linker.groupByTopic(new ArrayList<>());
        Assert.assertTrue(map2.isEmpty());
    }

    private List<Topic> createTopics(int topicSize) {
        List<Topic> topics = new ArrayList<>();
        for (int i = 0; i < topicSize; i++) {
            topics.add(new Topic(ImmutableSet.of(), i));
        }
        return topics;
    }

    private List<TopicAssignment> createAssignments(int assignmentSize) {
        List<TopicAssignment> assignments = new ArrayList<>();
        for (int i = 0; i < assignmentSize; i++) {
            assignments.add(new TopicAssignment(Character.valueOf((char) ((i + 65) % 127)).toString(), ImmutableSet.of(), i % TOPIC_SIZE));
        }
        return assignments;
    }

    @Test
    public void codeComponentWordMap() throws Exception {
        CodeElement c1 = new CodeElement("", "HelloWorld", Sets.newHashSet("hello", "world"));
        CodeElement c2 = new CodeElement("", "HelloWorld2", Sets.newHashSet("System", "out"));
        CodeElement c3 = new CodeElement("", null, null);
        ArrayList<CodeElement> codeElements = Lists.newArrayList(c1, c2, c3);

        Map<CodeElement, Collection<String>> codeElementSetMap = linker.codeComponentWordMap(codeElements);
        Assert.assertThat(codeElementSetMap.size(), is(2));
        Assert.assertTrue(codeElementSetMap.containsKey(c1));
        Assert.assertTrue(codeElementSetMap.containsKey(c2));
        Assert.assertThat(codeElementSetMap.get(c1), is(c1.getBag()));
        Assert.assertThat(codeElementSetMap.get(c2), is(c2.getBag()));
        Assert.assertFalse(codeElementSetMap.containsKey(c3));

        codeElementSetMap = linker.codeComponentWordMap(new ArrayList<>());
        Assert.assertThat(codeElementSetMap.size(), is(0));
    }
}
