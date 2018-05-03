package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.Label;
import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.service.ProjectService;
import ch.uzh.ifi.seal.changeadvisor.service.ReviewAggregationService;
import ch.uzh.ifi.seal.changeadvisor.web.dto.LabelWithReviews;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewDistributionReport;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewTimeSeriesData;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewsByTopLabelsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ReviewReportResource {

    private final ProjectService projectService;

    private final ReviewAggregationService aggregationService;

    public ReviewReportResource(ProjectService projectService, ReviewAggregationService aggregationService) {
        this.projectService = projectService;
        this.aggregationService = aggregationService;
    }

    @GetMapping(path = "reviews/{projectId}/time")
    public List<ReviewTimeSeriesData> reviewTimeSeries(@PathVariable("projectId") String projectId) {
        Optional<Project> project = projectService.findById(projectId);
        List<ReviewTimeSeriesData> report = project.map(p -> aggregationService.timeSeries(p.getGooglePlayId())).orElseThrow(IllegalArgumentException::new);
        return report;
    }

    @GetMapping(path = "reviews/{projectId}/distribution")
    public ResponseEntity<ReviewDistributionReport> distributionReport(@PathVariable("projectId") String projectId, @RequestParam("countOnly") boolean countOnly) {
        Optional<Project> project = projectService.findById(projectId);

        if (project.isPresent()) {
            ReviewDistributionReport report;
            if (countOnly) {
                report = aggregationService.groupByCategoriesCountOnly(project.get().getGooglePlayId());
            } else {
                report = aggregationService.groupByCategories(project.get().getGooglePlayId());
            }
            return ResponseEntity.ok(report);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(path = "reviews/top")
    public List<Label> topNLabels(@RequestBody ReviewsByTopLabelsDto dto) {
        return aggregationService.topNLabels(dto);
    }

    @PostMapping(path = "reviews/labels")
    public ResponseEntity<List<LabelWithReviews>> reviewsByTopNLabels(@RequestBody ReviewsByTopLabelsDto dto) {
        Optional<Project> project = projectService.findById(dto.getApp());
        Optional<List<LabelWithReviews>> labels = project.map(p -> aggregationService.reviewsByTopNLabels(new ReviewsByTopLabelsDto(p.getAppName(), p.getGooglePlayId(), dto.getCategory(), dto.getLimit(), dto.getNgrams())));
        return labels.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(path = "reviews/labels/category")
    public List<LabelWithReviews> reviewsByTopNLabelsByCategory(@RequestBody ReviewsByTopLabelsDto dto) {
        return aggregationService.reviewsByTopNLabelsByCategory(dto);
    }

}
