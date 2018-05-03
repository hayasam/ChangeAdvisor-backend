package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.service.ReviewImportService;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.JobExecution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyMapOf;

public class ReviewControllerTest {

    @Mock
    private ReviewImportService service;

    @Mock
    private JobExecution jobExecution;

    private final Long jobId = 99L;

    @InjectMocks
    private ReviewController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        given(service.reviewImport(anyMapOf(String.class, Object.class)))
                .willReturn(jobExecution);
        given(jobExecution.getJobId()).willReturn(jobId);
    }

    @Test
    public void reviewImport() throws Exception {
        List<String> apps = Lists.newArrayList("whatsapp", "2048");
        int limit = 50;
        Map<String, Object> params = new HashMap<>();
        params.put("apps", apps);
        params.put("limit", limit);

        long id = controller.reviewImport(params);
        Assert.assertThat(id, is(jobId));
    }

    @Test(expected = IllegalArgumentException.class)
    public void reviewImportNullMap() throws Exception {
        controller.reviewImport(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void reviewImportEmptyMap() throws Exception {
        controller.reviewImport(new HashMap<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void reviewImportNoAppsProvided() throws Exception {
        Map<String, Object> mapWithoutApps = new HashMap<>();
        mapWithoutApps.put("limit", 50);
        controller.reviewImport(mapWithoutApps);
    }
}
