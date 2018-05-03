package ch.uzh.ifi.seal.changeadvisor.preprocessing;

import org.tartarus.snowball.ext.PorterStemmer;

/**
 * Porter Stemmer.
 * Created by alex on 14.07.2017.
 */
public class Stemmer {

    private static PorterStemmer stemmer = new PorterStemmer();

    private Stemmer() {
    }

    /**
     * Stems the given token using Porter's stemmer.
     *
     * @param token token to stem.
     * @return stemmed token.
     */
    public static String stem(String token, int minWordLength) {
        if (token.length() < minWordLength) {
            return token;
        }

        stemmer.setCurrent(token);
        stemmer.stem();
        token = stemmer.getCurrent();
        return token;
    }
}
