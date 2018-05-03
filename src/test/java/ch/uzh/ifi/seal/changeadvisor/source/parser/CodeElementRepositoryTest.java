package ch.uzh.ifi.seal.changeadvisor.source.parser;

import ch.uzh.ifi.seal.changeadvisor.ChangeadvisorApplication;
import ch.uzh.ifi.seal.changeadvisor.MongoTestConfig;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElement;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElementRepository;
import com.google.common.collect.Sets;
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
public class CodeElementRepositoryTest {

    @Autowired
    private CodeElementRepository repository;

    @Before
    public void setUp() throws Exception {
        repository.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        repository.deleteAll();
    }

    @Test
    public void test() throws Exception {
        CodeElement codeElement = new CodeElement("", "HelloWorld", Sets.newHashSet("hello", "world"));
        codeElement = repository.save(codeElement);


        CodeElement element = repository.findById(codeElement.getId()).get();
        Assert.assertThat(element, is(codeElement));
    }
}
