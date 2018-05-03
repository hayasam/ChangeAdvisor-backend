package ch.uzh.ifi.seal.changeadvisor.preprocessing;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Created by alex on 20.07.2017.
 */
public class AnnotatedToken {

    public static final AnnotatedToken EMPTY_TOKEN = new AnnotatedToken("", "", "", false);
    private static final Set<String> PLURAL_NOUN_TAGS = ImmutableSet.of(
            "NNS", // Noun, plural
            "NNPS" // Proper noun, plural
    );
    private String token;

    private String posTag;

    private String lemma;

    private boolean isStopWord;

    public AnnotatedToken(String token, String posTag, String lemma, boolean isStopWord) {
        this.token = token;
        this.posTag = posTag;
        this.lemma = lemma;
        this.isStopWord = isStopWord;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPosTag() {
        return posTag;
    }

    public boolean isStopWord() {
        return isStopWord;
    }

    public String getLemma() {
        return lemma;
    }

    public void singularize() {
        if (isPlural()) {
            token = lemma;
        }
    }

    private boolean isPlural() {
        return PLURAL_NOUN_TAGS.contains(token);
    }

    public void stem() {
        token = Stemmer.stem(token, 3);
    }

    public int length() {
        return token.length();
    }

    public boolean isEmpty() {
        return token.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnnotatedToken that = (AnnotatedToken) o;

        if (isStopWord != that.isStopWord) return false;
        if (token != null ? !token.equals(that.token) : that.token != null) return false;
        if (posTag != null ? !posTag.equals(that.posTag) : that.posTag != null) return false;
        return lemma != null ? lemma.equals(that.lemma) : that.lemma == null;
    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (posTag != null ? posTag.hashCode() : 0);
        result = 31 * result + (lemma != null ? lemma.hashCode() : 0);
        result = 31 * result + (isStopWord ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AnnotatedToken{" +
                "token='" + token + '\'' +
                ", posTag='" + posTag + '\'' +
                '}';
    }
}
