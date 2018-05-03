package ch.uzh.ifi.seal.changeadvisor.source;

import ch.uzh.ifi.seal.changeadvisor.service.ProjectService;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SourceProcessingJobFactory {

    private static final String SOURCE_IMPORT = "sourceProcessing";

    private static final String STEP_NAME = "sourceProcessingStep";

    private final StepBuilderFactory stepBuilderFactory;

    private final JobBuilderFactory jobBuilderFactory;

    private final ProjectService projectService;

    @Autowired
    public SourceProcessingJobFactory(StepBuilderFactory stepBuilderFactory, JobBuilderFactory jobBuilderFactory, ProjectService projectService) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.projectService = projectService;
    }

    public Job job(SourceCodeDirectoryDto dto) {
        return jobBuilderFactory.get(SOURCE_IMPORT)
                .incrementer(new RunIdIncrementer())
                .flow(sourceImport(dto))
                .end()
                .build();
    }

    private Step sourceImport(SourceCodeDirectoryDto dto) {
        SourceImportTasklet importTasklet = new SourceImportTasklet(dto, projectService);
        return stepBuilderFactory.get(STEP_NAME)
                .allowStartIfComplete(true)
                .tasklet(importTasklet)
                .build();
    }

}
