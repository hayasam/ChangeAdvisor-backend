package ch.uzh.ifi.seal.changeadvisor.ml.util;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;

public class DefaultMapTest {

    @Test
    public void getIndexOfTopNValues() throws Exception {
        DefaultMap<Integer, Double> map = new DefaultMap<>(1.0);
        map.put(1, 1.0);
        map.put(2, 3.0);
        map.put(3, 5.0);

        DefaultMap<Integer, Double> map2 = new DefaultMap<>(1.0);
        map2.put(1, 1.0);
        map2.put(2, 3.0);
        map2.put(3, 5.0);
        map2.put(4, 3.0);

        List<Integer> indexOfTopNValues = map.getIndexOfTopNValues(3);
        Assert.assertThat(indexOfTopNValues, is(Lists.newArrayList(3, 2, 1)));

        indexOfTopNValues = map.getIndexOfTopNValues(1);
        Assert.assertThat(indexOfTopNValues, is(Lists.newArrayList(3)));

        indexOfTopNValues = map.getIndexOfTopNValues(10);
        Assert.assertThat(indexOfTopNValues, is(Lists.newArrayList(3, 2, 1)));

        indexOfTopNValues = map2.getIndexOfTopNValues(4);
        Assert.assertThat(indexOfTopNValues, is(Lists.newArrayList(3, 2, 4, 1)));

        indexOfTopNValues = map2.getIndexOfTopNValues(-1);
        Assert.assertThat(indexOfTopNValues, is(new ArrayList<>()));
    }
}