package ch.uzh.ifi.seal.changeadvisor.ml.math;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class SimpleMultinomialTest {

    @Test
    public void sample() throws Exception {
        List<Double> test = Lists.newArrayList(0.1, 0.2, 0.7);
        SimpleMultinomial multinomial = new SimpleMultinomial();
        multinomial.init(test);

        for (int i = 0; i < 20; i++) {
            int sample = multinomial.sample();
            Assert.assertTrue(sample < test.size());
        }
    }
}