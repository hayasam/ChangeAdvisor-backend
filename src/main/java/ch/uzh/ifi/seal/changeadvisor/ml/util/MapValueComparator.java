package ch.uzh.ifi.seal.changeadvisor.ml.util;

import java.util.Comparator;
import java.util.Map;

/**
 * Comparator implementation that compares indexes in a map by their value.
 */
public class MapValueComparator implements Comparator<Integer> {

    private final Map<Integer, Double> map;

    public MapValueComparator(Map<Integer, Double> map) {
        this.map = map;
    }

    @Override
    public int compare(Integer k1, Integer k2) {
        Double v1 = map.get(k1);
        Double v2 = map.get(k2);
        if (v1.equals(v2)) {
            return 1;
        }
        return v2.compareTo(map.get(k1));
    }
}