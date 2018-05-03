package ch.uzh.ifi.seal.changeadvisor.ml.util;

import org.ardoc.Result;

public class MockResult extends Result {

    public MockResult(String sentence, String category) {
        setSentence(sentence);
        setSentenceClass(category);
    }
}
