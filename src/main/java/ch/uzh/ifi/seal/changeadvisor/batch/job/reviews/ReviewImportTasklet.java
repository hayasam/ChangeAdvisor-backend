package ch.uzh.ifi.seal.changeadvisor.batch.job.reviews;

import config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.ArrayList;

public class ReviewImportTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(ReviewImportTasklet.class);

    private ArrayList<String> apps;

    private ConfigurationManager config;

    ReviewImportTasklet(ArrayList<String> apps, ConfigurationManager config) {
        this.apps = apps;
        this.config = config;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.info(String.format("Starting review crawling for app %s with parameters: limit=%s; threads=%s", apps, config.getLimit(), config.getNumberOfThreadToUse()));
        
        MonitorableExtractor extractor = new MonitorableExtractor(apps, config);
        extractor.extract();

        while (!extractor.isDone()) {
            Thread.sleep(2000); // do not refresh context too often.
            writeIntoExecutionContext(chunkContext, extractor.getProgress());
        }
        return RepeatStatus.FINISHED;
    }

    private <T> void writeIntoExecutionContext(ChunkContext context, T progress) {
        context.getStepContext().getStepExecution().getExecutionContext().put("extractor.progress", progress);
    }
}
