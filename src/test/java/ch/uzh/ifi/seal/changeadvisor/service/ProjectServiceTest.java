package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.ChangeadvisorApplication;
import ch.uzh.ifi.seal.changeadvisor.MongoTestConfig;
import ch.uzh.ifi.seal.changeadvisor.project.Project;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ChangeadvisorApplication.class, MongoTestConfig.class})
@ActiveProfiles("test")
public class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    private static final String APP_NAME = "com.frostwire";

    private static final String PATH = "localhost";

    private static final String EMPTY_CRON_EXPR = "";

    private static final String CRON_EXPR = "0 * * ? * *";

    @Test
    public void emptyCronExpression() throws Exception {
        Project p = new Project(APP_NAME, PATH, PATH, EMPTY_CRON_EXPR);
        Assert.assertThat(p.getAppName(), is(APP_NAME));
        Assert.assertThat(p.getPath(), is(PATH));
        Assert.assertThat(p.getRemoteUrl(), is(PATH));
        Assert.assertThat(p.getCronSchedule(), is(EMPTY_CRON_EXPR));
    }

    @Test
    public void cronExpression() throws Exception {
        Project p = new Project(APP_NAME, PATH, PATH, CRON_EXPR);
        Assert.assertThat(p.getAppName(), is(APP_NAME));
        Assert.assertThat(p.getPath(), is(PATH));
        Assert.assertThat(p.getRemoteUrl(), is(PATH));
        Assert.assertThat(p.getCronSchedule(), is(CRON_EXPR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCronExpression() throws Exception {
        Project p = new Project(APP_NAME, PATH, PATH, "adsfasdf");
    }

    @Test(expected = DuplicateKeyException.class)
    public void appNameUnique() throws Exception {
        Project p1 = new Project(APP_NAME, PATH, PATH, CRON_EXPR);
        Project p2 = new Project(APP_NAME, "", "", CRON_EXPR);
        p1 = projectService.save(p1);
        p2 = projectService.save(p2);
    }
}