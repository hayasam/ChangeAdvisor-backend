package ch.uzh.ifi.seal.changeadvisor.tfidf;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.util.CollectionUtils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private static final CoreLabelTokenFactory tokenFactory = new CoreLabelTokenFactory();

    public List<String> tokenize(String text) {
        PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader(text.toLowerCase()), tokenFactory, "");
        List<String> tokens = new ArrayList<>((int) (text.length() / 4.));
        while (ptbt.hasNext()) {
            CoreLabel label = ptbt.next();
            tokens.add(label.toString());
        }

        return tokens;
    }

    public List<List<String>> tokenize(String text, int n) {
        List<String> tokens = tokenize(text);
        return CollectionUtils.getNGrams(tokens, n, n);
    }
}
