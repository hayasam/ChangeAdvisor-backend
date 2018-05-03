package ch.uzh.ifi.seal.changeadvisor.preprocessing;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

/**
 * Created by alex on 14.07.2017.
 */
public class StemmerTest {

    @Test
    public void stem() throws Exception {
        String token = "Albums";
        String stem = Stemmer.stem(token, 3);
        String stem2 = Stemmer.stem("artist", 3);

        Assert.assertThat(stem, is("Album"));
        Assert.assertThat(stem2, is("artist"));
    }
}