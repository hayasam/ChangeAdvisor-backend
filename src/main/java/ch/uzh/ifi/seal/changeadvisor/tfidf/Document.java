package ch.uzh.ifi.seal.changeadvisor.tfidf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Document {

    private List<AbstractNGram> documentNgrams;

    public Document(List<AbstractNGram> documentNgrams) {
        this.documentNgrams = documentNgrams;
    }

    public int size() {
        return documentNgrams.size();
    }

    public double frequency(AbstractNGram token) {
        if (documentNgrams.isEmpty()) {
            return 0.0;
        }

        double count = 0;
        for (AbstractNGram word : documentNgrams) {
            if (word.equals(token)) {
                count += 1;
            }
        }
        return count / size();
    }

    public boolean contains(AbstractNGram token) {
        return documentNgrams.contains(token);
    }

    public List<AbstractNGram> tokens() {
        return documentNgrams;
    }

    public List<AbstractNGram> uniqueTokens() {
        return new ArrayList<>(new HashSet<>(documentNgrams));
    }
}
