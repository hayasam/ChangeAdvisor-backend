package ch.uzh.ifi.seal.changeadvisor.batch.job.reviews;

import config.ConfigurationManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;

public class ReviewsConfigurationManagerTest {

    @Test
    public void getConfig() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("export_to", "csv");
        params.put("limit", 9999);

        ReviewsConfigurationManager configurationManager = ReviewsConfigurationManager.from(params);
        ConfigurationManager config = configurationManager.getConfig();
        Assert.assertThat(config.getLimit(), is(9999));
        Assert.assertThat(config.getHowToStore(), is("csv"));
    }

    @Test
    public void cleanParams() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 200);
        params.put("limitasdf", -1);

        ReviewsConfigurationManager configurationManager = new ReviewsConfigurationManager();

        Map<String, String> clean = configurationManager.cleanParams(params);

        Assert.assertTrue(clean.containsKey("limit"));
        Assert.assertFalse(clean.containsKey("limitasdf"));
    }

    @Ignore
    @Test
    public void mergeUserConfigWithProperties() throws Exception {
        ReviewsConfigurationManager reviewImportJobFactory = new ReviewsConfigurationManager();

        ConfigurationManager config = ConfigurationManager.getInstance();

        Thread.sleep(1000);

        Assert.assertThat(config.getLimit(), is(200));
        Assert.assertThat(config.getStoreToCrawl(), is("google"));
        Assert.assertThat(config.getHowToStore(), is("mongodb"));

        Map<String, String> params = new HashMap<>();
        params.put("limit", "2000");
        params.put("export_to", "csv");
        config = reviewImportJobFactory.mergeUserConfigWithProperties(params);

        Assert.assertThat(config.getLimit(), is(2000));
        Assert.assertThat(config.getStoreToCrawl(), is("google"));
        Assert.assertThat(config.getHowToStore(), is("csv"));
    }
}