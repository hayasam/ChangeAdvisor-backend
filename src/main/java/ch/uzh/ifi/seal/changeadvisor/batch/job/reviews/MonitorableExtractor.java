package ch.uzh.ifi.seal.changeadvisor.batch.job.reviews;

import config.ConfigurationManager;
import crawler.GoogleReviewsCrawler;
import extractors.Extractor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class MonitorableExtractor extends Extractor {

    private static final Logger logger = LoggerFactory.getLogger(MonitorableExtractor.class);

    private ConfigurationManager configurationManager;

    private Map<String, GoogleReviewsCrawler> crawlers = new HashMap<>();

    private List<Future<?>> crawlersRunning;

    public MonitorableExtractor(ArrayList<String> appsToMine, ConfigurationManager configurationManager) {
        super(appsToMine);
        this.configurationManager = configurationManager;
    }

    @Override
    public void extract() {
        final int numberOfThreadToUse = this.configurationManager.getNumberOfThreadToUse();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreadToUse);

        for (String app : appsToMine) {
            GoogleReviewsCrawler crawler = new GoogleReviewsCrawler(app, configurationManager);
            crawlers.put(app, crawler);
        }

        crawlersRunning = crawlers.values().stream().map(executor::submit).collect(Collectors.toList());

        executor.shutdown();
    }

    public Map<String, Integer> getProgress() {
        Map<String, Integer> progress = new ConcurrentHashMap<>(crawlers.size());
        crawlers.forEach((key, value) -> progress.put(key, getReviewsCounter(value)));
        return progress;
    }

    private Integer getReviewsCounter(GoogleReviewsCrawler crawler) {
        Integer reviewsCounter = 0;
        try {
            reviewsCounter = (Integer) FieldUtils.readDeclaredField(crawler, "reviewsCounter", true);
        } catch (IllegalAccessException e) {
            logger.error("Failed to get review counter.", e);
        }
        return reviewsCounter;
    }

    public boolean isDone() {
        return crawlersRunning.stream().allMatch(Future::isDone);
    }
}