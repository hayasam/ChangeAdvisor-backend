package ch.uzh.ifi.seal.changeadvisor.tfidf;

import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.Label;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TfidfService {

    private TFiDF tFiDF = new TFiDF();

    public List<Label> computeTfidfScoreForTokens(String appName, String category, List<AbstractNGram> tokens, Document document, Corpus corpus) {
        return tokens.stream()
                .map(token -> new Label(appName, category, token, tFiDF.compute(token, document, corpus)))
                .collect(Collectors.toList());
    }
}
