package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkingResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkingResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LinkingResultResource {

    private final LinkingResultRepository repository;

    @Autowired
    public LinkingResultResource(LinkingResultRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = "/results")
    public List<LinkingResult> results() {
        return repository.findAll();
    }
}
