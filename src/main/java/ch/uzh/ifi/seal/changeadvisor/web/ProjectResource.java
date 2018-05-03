package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.project.ReviewsConfig;
import ch.uzh.ifi.seal.changeadvisor.schedule.ScheduledReviewImportConfig;
import ch.uzh.ifi.seal.changeadvisor.service.ProjectService;
import edu.emory.mathcs.backport.java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ProjectResource {

    private final ProjectService service;

    private final ScheduledReviewImportConfig scheduledReviewImportConfig;

    @Autowired
    public ProjectResource(ProjectService service, ScheduledReviewImportConfig scheduledReviewImportConfig) {
        this.service = service;
        this.scheduledReviewImportConfig = scheduledReviewImportConfig;
    }

    @GetMapping("/projects")
    public List<Project> getProjects() {
        List<Project> projects = service.findAll();
        Collections.sort(projects);
        return projects;
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable("projectId") final String projectId) {
        Optional<Project> project = service.findById(projectId);
        return project.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> saveProject(@RequestBody Project project) {
        if (project == null || !project.hasValidCronExpression()) {
            return ResponseEntity.badRequest().body(project);
        }

        if (project.getReviewsConfig() == null) {
            project.setReviewsConfig(new ReviewsConfig(null, null));
        }
        Project savedProject = service.save(project);

        if (savedProject.hasCronSchedule()) {
            scheduledReviewImportConfig.setSchedule(savedProject);
        }

        Optional<Project> updatedProject = service.findById(project.getId());
        return updatedProject.map(ResponseEntity::ok).orElse(ResponseEntity.badRequest().body(project));
    }
}
