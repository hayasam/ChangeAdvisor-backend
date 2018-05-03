package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.web.dto.ExecutionReport;
import ch.uzh.ifi.seal.changeadvisor.web.dto.StepExecutionReport;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JobHolder {

    private Map<Long, JobExecution> jobs = new HashMap<>();

    private JobExecution getJob(Long id) {
        return jobs.get(id);
    }

    public void addJob(JobExecution jobExecution) {
        if (jobExecution != null) {
            jobs.put(jobExecution.getJobId(), jobExecution);
        } else {
            throw new NullPointerException("Job execution cannot be null!");
        }
    }

    public boolean hasJob(Long id) {
        return jobs.containsKey(id);
    }

    public int size() {
        return jobs.size();
    }

    public boolean isEmpty() {
        return jobs.isEmpty();
    }

    public void clear() {
        jobs.clear();
    }

    /**
     * Generates an execution report out of a jobExecution instance.
     *
     * @param jobId id of jobExecution.
     * @return collection of reports of execution steps for the wanted job.
     * @see StepExecutionReport
     */
    public ExecutionReport executionReportForJob(Long jobId) {
        if (jobId != null && hasJob(jobId)) {
            JobExecution job = getJob(jobId);
            String jobName = job.getJobInstance().getJobName();
            Collection<StepExecution> stepExecutions = job.getStepExecutions();
            List<StepExecutionReport> stepReports = stepExecutions
                    .stream()
                    .map(StepExecutionReport::of)
                    .collect(Collectors.toList());
            return new ExecutionReport(jobName, stepReports);
        }
        throw new IllegalArgumentException(String.format("No job found for job id: %d", jobId));
    }
}
