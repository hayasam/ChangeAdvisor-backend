package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.source.SourceImportJobFactory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SourceCodeService {

    private final ProjectService projectService;

    private final JobService jobService;

    private final SourceImportJobFactory sourceImportJobFactory;

    @Autowired
    public SourceCodeService(ProjectService projectService, JobService jobService, SourceImportJobFactory sourceImportJobFactory) {
        this.projectService = projectService;
        this.jobService = jobService;
        this.sourceImportJobFactory = sourceImportJobFactory;
    }

    /**
     * Given a dto containing a path to a directory (File system or remote) and credentials (Optional),
     * adds the directory and project to the database for later retrieval.
     *
     * @param dto Value object containing the path to a project.
     * @return a job execution instance representing the adding of a project.
     * @throws ch.uzh.ifi.seal.changeadvisor.service.FailedToRunJobException if an exception occured while starting job.
     */
    public JobExecution startSourceCodeDownload(SourceCodeDirectoryDto dto) throws FailedToRunJobException {
        projectService.findById(dto.getProjectName());
        Job job = sourceImportJobFactory.importAndProcessingJob(dto);
        return jobService.run(job);
    }

    public JobExecution startSourceCodeProcessing(final String appName) throws FailedToRunJobException {
        Job job = sourceImportJobFactory.processingJob(appName);
        return jobService.run(job);
    }
}
