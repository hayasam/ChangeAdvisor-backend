package ch.uzh.ifi.seal.changeadvisor.ml.math;

import java.util.Collection;

public interface Multinomial {

    int sample();

    void init(Collection<Double> probabilities);
}
