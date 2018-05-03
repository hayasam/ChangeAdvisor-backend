package ch.uzh.ifi.seal.changeadvisor.ml.math;

import java.util.Collection;
import java.util.Random;

/**
 * Computes multinomial distribution of probabilities.
 * Note: probabilities must be normalized (i.e. sum of probabilities eq. 1).
 */
public class SimpleMultinomial implements Multinomial {

    private Random generator;

    private double[] distribution;

    private int distributionSize;

    public void init(Collection<Double> probabilities) {
        assertProbabilitiesAreOne(probabilities);
        generator = new Random();

        distributionSize = probabilities.size();
        distribution = new double[distributionSize];

        double position = 0;
        int i = 0;
        for (Double p : probabilities) {
            position += p;
            distribution[i++] = position;
        }

        distribution[distributionSize - 1] = 1.0;
    }

    private void assertProbabilitiesAreOne(Collection<Double> probabilities) {
        Double sum = probabilities.stream().reduce((d1, d2) -> d1 + d2).orElse(0.0);
        if (sum < 0) {
            throw new IllegalArgumentException("How is this possible!");
        }
        if (!(sum > 0.99 && sum < 1.01)) {
            throw new IllegalArgumentException("Sum of probabilities should be one. Was: " + sum + "\t" + probabilities.toString());
        }
    }

    public int sample() {
        double uniform = generator.nextDouble();
        for (int i = 0; i < distributionSize; ++i) {
            if (uniform < distribution[i]) {
                return i;
            }
        }
        return distributionSize - 1;
    }

}