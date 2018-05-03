package ch.uzh.ifi.seal.changeadvisor.preprocessing;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

/**
 * Created by alex on 17.07.2017.
 */
public class EnglishSpellCheckerTest {


    @Test
    public void checkAndCorrect() throws Exception {
        final String sentence = "A sentence with a error in the Hitchhiker's Guide tot he Galaxy";
        final String correctedSentence = "A sentence with an error in the Hitch-hiker's Guide to the Galaxy";
        EnglishSpellChecker spellChecker = new EnglishSpellChecker();

        String correction = spellChecker.correct(sentence);

        Assert.assertThat(correction, is(correctedSentence));
    }

    @Test
    public void checkAndCorrect2() throws Exception {
        final String sentence = "Thiss is som text wewant to ceck for typos";
        final String correctedSentence = "This is some text we want to check for typos";
        EnglishSpellChecker spellChecker = new EnglishSpellChecker();

        final String correction = spellChecker.correct(sentence);
        Assert.assertThat(correction, is(correctedSentence));
    }
}