package ch.uzh.ifi.seal.changeadvisor.tfidf;

public class TFiDF {

    public double compute(AbstractNGram token, Document document, Corpus documents) {
        return tf(token, document) * idf(token, documents);
    }

    double tf(AbstractNGram token, Document document) {
        return document.frequency(token);
    }

    double idf(AbstractNGram token, Corpus documents) {
        int documentFrequency = documents.documentFrequency(token);
        if (documentFrequency == 0) {
            return 0.0;
        }
        return Math.log10(documents.size() / (double) documentFrequency);
    }
}
