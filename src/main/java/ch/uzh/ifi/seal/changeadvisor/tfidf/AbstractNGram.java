package ch.uzh.ifi.seal.changeadvisor.tfidf;

public interface AbstractNGram<T> {

    T getTokens();

    int ngramSize();
}
