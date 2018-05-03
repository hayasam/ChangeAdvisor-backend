package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.web.JobHolder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JobServiceTest {

    @Mock
    private JobLauncher launcher;

    @Mock
    private JobHolder jobHolder;

    @Mock
    private Job job;

    @Mock
    private JobExecution jobExecution;

    @InjectMocks
    private JobService service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void run() throws Exception {
        JobParameters parameters = service.parametersWithCurrentTimestamp();

        when(launcher.run(eq(job), any(JobParameters.class))).thenReturn(jobExecution);

        service.run(job, parameters);
        verify(launcher).run(job, parameters);
        verify(jobHolder).addJob(any(JobExecution.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void runJobNull() throws Exception {
        service.run(null, service.parametersWithCurrentTimestamp());
    }

    @Test(expected = IllegalArgumentException.class)
    public void runParamsNull() throws Exception {
        service.run(job, null);
    }
}