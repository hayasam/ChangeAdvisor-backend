package ch.uzh.ifi.seal.changeadvisor.batch.job.reviews;

import com.google.common.collect.ImmutableMap;
import config.ConfigurationManager;
import org.apache.commons.lang3.SystemUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ReviewsConfigurationManager {

    private static final String PHANTOMJS_WIN = "lib/phantomjs_win.exe";

    private ConfigurationManager config;

    ReviewsConfigurationManager() {
    }

    /**
     * Gets the configuration manager for the reviews crawler.
     *
     * @return config.
     */
    public ConfigurationManager getConfig() {
        return config;
    }

    /**
     * Parses user-supplied parameters and creates a configuration manager from it.
     *
     * @param params user parameters.
     * @return configuration manager wrapper.
     */
    public static ReviewsConfigurationManager from(Map<String, Object> params) {
        ReviewsConfigurationManager reviewsConfigurationManager = new ReviewsConfigurationManager();
        Map<String, String> clean = reviewsConfigurationManager.cleanParams(params);
        reviewsConfigurationManager.config = reviewsConfigurationManager.mergeUserConfigWithProperties(clean);
        return reviewsConfigurationManager;
    }

    Map<String, String> cleanParams(Map<String, Object> params) {
        Map<String, String> cleaned = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (AllowedKeyword.isAllowed(entry.getKey())) {
                cleaned.put(entry.getKey(), entry.getValue().toString());
            }
        }
        return ImmutableMap.copyOf(cleaned);
    }

    ConfigurationManager mergeUserConfigWithProperties(Map<String, String> params) {
        ConfigurationManager config = ConfigurationManager.getInstance();
        Properties properties = configToProperties(config);
        properties.putAll(params);

        if (SystemUtils.IS_OS_WINDOWS) {
            properties.put(AllowedKeyword.PHANTOM_JS, PHANTOMJS_WIN);
        } else {
            properties.put(AllowedKeyword.PHANTOM_JS, config.getPathForPhantomJSDriver());
        }
        loadConfigFromProperties(config, properties);

        return config;
    }

    private Properties configToProperties(ConfigurationManager config) {
        Properties properties = new Properties();

        properties.put(AllowedKeyword.STORE, config.getStoreToCrawl());
        properties.put(AllowedKeyword.INPUT_FILE, config.getInputCsv());
        properties.put(AllowedKeyword.OUTPUT_FILE, config.getOutputCsv());

        Date startingDate = config.getStartingDate();
        Date endDate = config.getEndDate();
        properties.put(AllowedKeyword.FROM, startingDate == null ? "" : startingDate);
        properties.put(AllowedKeyword.TO, endDate == null ? "" : endDate);

        properties.put(AllowedKeyword.LIMIT, Integer.toString(config.getLimit()));
        properties.put(AllowedKeyword.THREAD, Integer.toString(config.getNumberOfThreadToUse()));
        properties.put(AllowedKeyword.PHANTOM_JS, config.getPathForPhantomJSDriver());
        properties.put(AllowedKeyword.REVIEWS_FOR, config.getReviewsOrder());
        properties.put(AllowedKeyword.EXPORT_TO, config.getHowToStore());
        return properties;
    }

    private void loadConfigFromProperties(ConfigurationManager config, Properties properties) {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            properties.store(bout, "");
            config.directLoadContent("byteConfig", bout.toString());
            ConfigurationManager.setDirectContent(bout.toString());
        } catch (IOException e) {
            throw new IllegalStateException("This should not have happened on write using ByteArrayOutputStream.", e);
        }
    }
}
