package ch.uzh.ifi.seal.changeadvisor.web;


import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkingResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewRepository;
import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.service.*;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewAnalysisDto;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewsByTopLabelsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewImportService reviewImportService;

    private final ReviewRepository repository;

    private final ArdocService ardocService;

    private final LabelLinkerService labelLinkerService;

    private final ProjectService projectService;

    @Autowired
    public ReviewController(ReviewImportService reviewImportService, ReviewRepository repository,
                            ArdocService ardocService, LabelLinkerService labelLinkerService, ProjectService projectService) {
        this.reviewImportService = reviewImportService;
        this.repository = repository;
        this.ardocService = ardocService;
        this.labelLinkerService = labelLinkerService;
        this.projectService = projectService;
    }

    @PostMapping(path = "reviews")
    public long reviewImport(@RequestBody Map<String, Object> params) throws FailedToRunJobException {
        Assert.notEmpty(params, "Empty or null parameters. Need at least list of apps.");
        Assert.isTrue(params.containsKey("apps") || params.containsKey("id"), "Request has to contain list of apps.");
        logger.info("Creating review import job and starting process with parameters %s.", params);
        JobExecution jobExecution = reviewImportService.reviewImport(params);
        return jobExecution.getJobId();
    }

    @GetMapping(path = "reviews")
    public Collection<Review> reviews() {
        return repository.findAll();
    }

    @PostMapping(path = "reviews/analyze")
    public long reviewAnalysis(@RequestBody @Valid ReviewAnalysisDto dto) throws FailedToRunJobException {
        logger.info("Starting analysis job for app %s!", dto.getApp());
        JobExecution jobExecution = reviewImportService.reviewAnalysis(dto);
        return jobExecution.getJobId();
    }

    @GetMapping(path = "reviews/lastAnalyzed")
    public ArdocResult lastResultAnalyzed(@RequestParam("app") String app) {
        if (StringUtils.isEmpty(app)) {
            throw new IllegalArgumentException("Need an app name!");
        }
        return ardocService.getLastAnalyzed(app);
    }

    @GetMapping(path = "reviews/sinceLastAnalyzed")
    public List<Review> reviewsSinceLastAnalyzed(@RequestParam("app") String app) {
        if (StringUtils.isEmpty(app)) {
            throw new IllegalArgumentException("Need an app name!");
        }
        return ardocService.getReviewsSinceLastAnalyzed(app);
    }

    @PostMapping(path = "reviews/processing")
    public long reviewsProcessing(@RequestBody @Valid ReviewAnalysisDto dto) throws FailedToRunJobException {
        logger.info("Starting reviews processing job for app %s!", dto.getApp());
        JobExecution jobExecution = reviewImportService.reviewProcessing(dto);
        return jobExecution.getJobId();
    }

    @PostMapping(path = "reviews/clustering")
    public long reviewsClustering(@RequestBody @Valid ReviewAnalysisDto dto) throws FailedToRunJobException {
        logger.info("Starting reviews clustering job for app %s!", dto.getApp());
        JobExecution jobExecution = reviewImportService.reviewClustering(dto);
        return jobExecution.getJobId();
    }

    @PostMapping(path = "reviews/labeling")
    public long reviewLabeling(@RequestBody ReviewAnalysisDto dto) throws FailedToRunJobException {
        JobExecution jobExecution = reviewImportService.reviewLabeling(dto);
        return jobExecution.getJobId();
    }

    @PostMapping(path = "reviews/linking")
    public List<LinkingResult> link(@RequestBody ReviewsByTopLabelsDto dto, @RequestParam("label") String label) {
        Project project = projectService.findByAppName(dto.getApp());
        dto = new ReviewsByTopLabelsDto(project.getAppName(), project.getGooglePlayId(), dto.getCategory(), dto.getLimit(), dto.getNgrams());
        List<LinkingResult> results = labelLinkerService.link(label, dto);
        results.sort(Comparator.comparing(LinkingResult::getSimilarity).reversed().thenComparing(LinkingResult::getCodeComponentName));
        return results;
    }
}
