package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewImportJobFactory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewAnalysisDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReviewImportService {

    private final ReviewImportJobFactory reviewImportJobFactory;

    private final JobService jobService;

    @Autowired
    public ReviewImportService(ReviewImportJobFactory reviewImportJobFactory, JobService jobService) {
        this.reviewImportJobFactory = reviewImportJobFactory;
        this.jobService = jobService;
    }

    public JobExecution reviewImport(Map<String, Object> params) throws FailedToRunJobException {
        Job reviewImport = reviewImportJobFactory.job(params);
        return jobService.run(reviewImport);
    }

    public JobExecution reviewAnalysis(ReviewAnalysisDto dto) throws FailedToRunJobException {
        Job reviewAnalysis = reviewImportJobFactory.reviewAnalysis(dto);
        return jobService.run(reviewAnalysis);
    }

    public JobExecution reviewProcessing(ReviewAnalysisDto dto) throws FailedToRunJobException {
        Job reviewProcessing = reviewImportJobFactory.reviewProcessing(dto);
        return jobService.run(reviewProcessing);
    }

    public JobExecution reviewClustering(ReviewAnalysisDto dto) throws FailedToRunJobException {
        Job reviewClustering = reviewImportJobFactory.reviewClustering(dto);
        return jobService.run(reviewClustering);
    }

    public JobExecution reviewLabeling(ReviewAnalysisDto dto) throws FailedToRunJobException {
        Job reviewLabeling = reviewImportJobFactory.labelReviews(dto);
        return jobService.run(reviewLabeling);
    }
}
