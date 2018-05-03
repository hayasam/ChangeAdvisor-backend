package ch.uzh.ifi.seal.changeadvisor.tfidf;

import org.junit.Assert;

public class Unigram implements AbstractNGram<String> {

    private final String token;

    public Unigram(String token) {
        Assert.assertNotNull(token, "Token cannot be null!");
        this.token = token;
    }

    @Override
    public String getTokens() {
        return token;
    }

    @Override
    public int ngramSize() {
        return 1;
    }

    @Override
    public String toString() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Unigram unigram = (Unigram) o;

        return token.equals(unigram.token);
    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }
}
