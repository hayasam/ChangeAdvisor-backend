package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.service.FailedToRunJobException;
import ch.uzh.ifi.seal.changeadvisor.service.ProjectService;
import ch.uzh.ifi.seal.changeadvisor.service.SourceCodeService;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class SourceCodeController {

    private static final Logger logger = LoggerFactory.getLogger(SourceCodeController.class);

    private final SourceCodeService sourceCodeService;

    private final ProjectService projectService;

    @Autowired
    public SourceCodeController(SourceCodeService sourceCodeService, ProjectService projectService) {
        this.sourceCodeService = sourceCodeService;
        this.projectService = projectService;
    }

    @PostMapping(path = "source")
    public long downloadSourceCode(@RequestBody @Valid SourceCodeDirectoryDto dto) throws FailedToRunJobException {
        Assert.isTrue(projectService.projectExists(dto.getProjectName()), "Project doesn't exists!");
        JobExecution jobExecution = sourceCodeService.startSourceCodeDownload(dto);
        return jobExecution.getJobId();
    }

    @PostMapping(path = "source/{projectId}")
    public long downloadSourceCode(@PathVariable("projectId") String projectId) {
        Assert.notNull(projectId, "Project id cannot be null!");
        Optional<Project> project = projectService.findById(projectId);
        JobExecution jobExecution = project.map(p -> {
            try {
                JobExecution execution = sourceCodeService.startSourceCodeDownload(new SourceCodeDirectoryDto(p.getRemoteUrl(), p.getAppName()));
                p.justImportedSource();
                p = projectService.save(p);
                return execution;
            } catch (FailedToRunJobException e) {
                logger.error("Failed to start source code download and process.", e);
            }
            return null;
        }).orElseThrow(() -> new IllegalArgumentException("No project found for id: [%s]"));
        return jobExecution.getJobId();
    }

    @PostMapping(path = "source/processing")
    public long processSourceCode(@RequestBody Project project) throws FailedToRunJobException {
        if (StringUtils.isEmpty(project.getAppName())) {
            throw new IllegalArgumentException(String.format("Invalid Path Variable \"%s\"", project.getAppName()));
        }
        JobExecution jobExecution = sourceCodeService.startSourceCodeProcessing(project.getAppName());
        return jobExecution.getJobId();
    }

}
