package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.web.dto.ExecutionReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusResource {

    private final JobHolder jobHolder;

    @Autowired
    public StatusResource(JobHolder jobHolder) {
        this.jobHolder = jobHolder;
    }

    @GetMapping(path = "status/{jobId}")
    @ResponseBody
    public ExecutionReport reviewAnalysis(@PathVariable(name = "jobId") Long jobId) {
        return jobHolder.executionReportForJob(jobId);
    }
}
