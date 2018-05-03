package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElement;

import java.util.Collection;
import java.util.List;

public interface Linker {

    List<LinkingResult> process(Collection<TopicAssignment> assignments, Collection<CodeElement> codeElements);

    List<LinkingResult> link(String topicId, Collection<? extends LinkableReview> reviews, Collection<CodeElement> codeElements);
}
