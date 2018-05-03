package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.ChangeAdvisorLinker;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkingResult;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElement;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElementRepository;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewsByTopLabelsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class LabelLinkerService {

    private final CodeElementRepository codeElementRepository;

    private final ChangeAdvisorLinker linker;

    private final LabelService labelService;

    @Autowired
    public LabelLinkerService(CodeElementRepository codeElementRepository, ChangeAdvisorLinker linker, LabelService labelService) {
        this.codeElementRepository = codeElementRepository;
        this.linker = linker;
        this.labelService = labelService;
    }

    /**
     * Runs the ChangeAdvisor linker with the reviews fetched from the label and code elements.
     *
     * @param dto object containing the app name and category for reviews.
     * @return changeadvisor linking results.
     */
    public List<LinkingResult> link(String token, ReviewsByTopLabelsDto dto) {
        List<TransformedFeedback> feedback = labelService.getFeedbackCorrespondingToLabel(token, dto.getGooglePlayId(), dto.getCategory());
        List<CodeElement> codeElements = codeElementRepository.findByAppName(dto.getApp());
        return linker.link(UUID.randomUUID().toString(), feedback, codeElements);
    }
}
