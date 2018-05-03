package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.project.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public boolean projectExists(final String appName) {
        return repository.existsByAppName(appName);
    }

    public List<Project> findAll() {
        return repository.findAll();
    }

    public Optional<Project> findById(final String projectId) {
        if (projectId == null) {
            return Optional.empty();
        }
        return repository.findById(projectId);
    }

    public Project findByAppName(final String appName) {
        return repository.findByAppName(appName);
    }

    public Project save(Project project) {
        return repository.save(project);
    }
}
