package ch.uzh.ifi.seal.changeadvisor.ml.math;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;

public class MockMultinomialTest {
    @Test
    public void sample() throws Exception {
        MockMultinomial multinomial = new MockMultinomial();
        multinomial.init(new ArrayList<>());
        for (int i = 0; i < 100; i++) {
            Assert.assertThat(multinomial.sample(), is(0));
        }
    }

}