package ch.uzh.ifi.seal.changeadvisor.batch.job.sourcecode;

import ch.uzh.ifi.seal.changeadvisor.preprocessing.CorpusProcessor;
import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElement;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElementRepository;
import ch.uzh.ifi.seal.changeadvisor.source.parser.bean.ClassBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * Created by alex on 22.07.2017.
 */
public class SourceCodeProcessor implements ItemProcessor<ClassBean, CodeElement> {

    private static final String DIRECTORY_KEY = "directory";

    private static final Logger logger = LoggerFactory.getLogger(SourceCodeProcessor.class);

    private final int threshold;

    private CorpusProcessor corpusProcessor;

    private String appName = "";

    private CodeElementRepository codeElementRepository;

    public SourceCodeProcessor(int threshold, CorpusProcessor corpusProcessor, CodeElementRepository codeElementRepository) {
        this.threshold = threshold;
        Assert.notNull(corpusProcessor, "CorpusProcessor cannot be null.");
        Assert.notNull(codeElementRepository, "CodeElementRepository cannot be null.");
        this.corpusProcessor = corpusProcessor;
        this.codeElementRepository = codeElementRepository;
    }

    @SuppressWarnings("unused")
    @BeforeStep
    public void getProjectNameAndDeletePreviousProcessedCode(StepExecution stepExecution) {
        Project directory = getDirectoryFromStepExecutionContext(stepExecution);
        appName = directory.getAppName();
        Assert.isTrue(!StringUtils.isEmpty(appName), "Didn't find any project name in step context!");
        logger.info(String.format("Found app name [%s] in step context.", appName));
        codeElementRepository.deleteByAppName(appName);
    }

    private Project getDirectoryFromStepExecutionContext(StepExecution stepExecution) {
        Object project = stepExecution.getJobExecution().getExecutionContext().get(DIRECTORY_KEY);
        if (project == null || !Project.class.isInstance(project)) {
            throw new IllegalArgumentException(String.format("Couldn't find project in Step Context. Found %s", project));
        }
        return (Project) project;
    }

    @Override
    public CodeElement process(ClassBean item) throws Exception {
        Collection<String> bag = corpusProcessor.process(item.getPublicCorpus());
        if (isBelowThreshold(bag)) {
            return null;
        }
        return new CodeElement(appName, item.getFullyQualifiedClassName(), bag);
    }

    private boolean isBelowThreshold(Collection<String> bag) {
        return bag.size() < threshold;
    }
}
