package ch.uzh.ifi.seal.changeadvisor.ml.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MapValueComparatorTest {
    @Test
    public void compare() throws Exception {
        Map<Integer, Double> map = new HashMap<>();
        map.put(1, 1.0);
        map.put(2, 3.0);
        map.put(3, 5.0);

        MapValueComparator valueComparator = new MapValueComparator(map);

        int compare = valueComparator.compare(1, 2);
        Assert.assertTrue(compare > 0);

        compare = valueComparator.compare(2, 1);
        Assert.assertTrue(compare < 0);

        compare = valueComparator.compare(1, 1);
        Assert.assertTrue(compare == 1);
    }

}