package ch.uzh.ifi.seal.changeadvisor.batch.job;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Overall ChangeAdvisor Job configuration.
 * Created by alex on 15.07.2017.
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {

//    private static final String TEST_DIRECTORY = "test_files_parser";
//
//    private static final String FROSTWIRE_DIRECTORY = "/com.frostwire.android";
//
//    private static final String JOB_NAME = "changeAdvisor";
//
//    private static final String SOURCE_CODE_JOB = "sourceCodeImport";
//
//    private static final String REVIEW_JOB = "reviewImport";
//
//    private final JobBuilderFactory jobBuilderFactory;
//
//    private final SourceComponentsTransformationStepConfig sourceComponentStepConfig;
//
//    private final ArdocStepConfig ardocStepConfig;
//
//    private final FeedbackTransformationStepConfig transformationStepConfig;
//
//    private final DocumentClusteringStepConfig documentClusteringStepConfig;
//
//    private final LinkingStepConfig linkingStepConfig;
//
//    @Autowired
//    public BatchConfig(JobBuilderFactory jobBuilderFactory, SourceComponentsTransformationStepConfig sourceComponentStepConfig, ArdocStepConfig ardocStepConfig, FeedbackTransformationStepConfig transformationStepConfig, DocumentClusteringStepConfig documentClusteringStepConfig, LinkingStepConfig linkingStepConfig) {
//        this.jobBuilderFactory = jobBuilderFactory;
//        this.sourceComponentStepConfig = sourceComponentStepConfig;
//        this.ardocStepConfig = ardocStepConfig;
//        this.transformationStepConfig = transformationStepConfig;
//        this.documentClusteringStepConfig = documentClusteringStepConfig;
//        this.linkingStepConfig = linkingStepConfig;
//    }
//
////    @Bean
////    public Job changeAdvisor(JobCompletionNotificationListener listener) {
////        documentClusteringStepConfig.setMaxIterations(50);
////        return jobBuilderFactory.get(JOB_NAME)
////                .incrementer(new RunIdIncrementer())
////                .listener(listener)
////                .flow(sourceComponentStepConfig.extractBagOfWords(TEST_DIRECTORY + FROSTWIRE_DIRECTORY))
////                .next(ardocStepConfig.ardocAnalysis())
////                .next(transformationStepConfig.transformFeedback())
////                .next(documentClusteringStepConfig.documentsClustering())
////                .next(linkingStepConfig.clusterLinking())
////                .end()
////                .build();
////    }
}
