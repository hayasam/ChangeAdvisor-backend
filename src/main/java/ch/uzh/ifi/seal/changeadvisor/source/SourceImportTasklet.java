package ch.uzh.ifi.seal.changeadvisor.source;

import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.service.ProjectService;
import ch.uzh.ifi.seal.changeadvisor.source.importer.SourceCodeImporter;
import ch.uzh.ifi.seal.changeadvisor.source.importer.SourceCodeImporterFactory;
import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class SourceImportTasklet implements Tasklet {

    private SourceCodeDirectoryDto dto;

    private final ProjectService projectService;

    public SourceImportTasklet(SourceCodeDirectoryDto dto, ProjectService projectService) {
        this.dto = dto;
        this.projectService = projectService;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        SourceCodeImporter importer = SourceCodeImporterFactory.getImporter(dto);
        SourceCodeDirectory sourceCodeDirectory = importer.importSource();
        Project project = saveOrUpdateDirectory(sourceCodeDirectory);

        writeIntoExecutionContext(chunkContext, project);
        return RepeatStatus.FINISHED;
    }

    private Project saveOrUpdateDirectory(SourceCodeDirectory directory) {
        Project project = projectService.findByAppName(directory.getProjectName());
        project.setSourceCodeDirectory(directory);
        project = projectService.save(project);
        return project;
    }

    private <T> void writeIntoExecutionContext(ChunkContext context, T item) {
        context.getStepContext().getStepExecution().getExecutionContext().put("directory", item);
    }
}
