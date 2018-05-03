package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.web.JobHolder;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JobService {

    private final JobLauncher jobLauncher;

    private final JobHolder jobHolder;

    @Autowired
    public JobService(JobLauncher jobLauncher, JobHolder jobHolder) {
        this.jobLauncher = jobLauncher;
        this.jobHolder = jobHolder;
    }

    public JobExecution run(Job job) throws FailedToRunJobException {
        return run(job, parametersWithCurrentTimestamp());
    }

    public JobExecution run(Job job, JobParameters parameters) throws FailedToRunJobException {
        if (job == null || parameters == null) {
            throw new IllegalArgumentException(String.format("Job and parameters must both be not null. Got %s, %s", job, parameters));
        }
        try {
            JobExecution jobExecution = jobLauncher.run(job, parameters);
            jobHolder.addJob(jobExecution);
            return jobExecution;
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobParametersInvalidException | JobInstanceAlreadyCompleteException e) {
            throw new FailedToRunJobException("Failed to start job.", e);
        }
    }

    JobParameters parametersWithCurrentTimestamp() {
        return new JobParametersBuilder().addDate("timestamp", new Date()).toJobParameters();
    }
}
