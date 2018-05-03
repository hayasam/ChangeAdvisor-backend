package ch.uzh.ifi.seal.changeadvisor.ml.math;

import cc.mallet.util.Maths;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

public class VectorTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void init() throws Exception {
        final int n = 3;
        Vector<Integer> v1 = new Vector<>(n, 0);

        List<Integer> list = Lists.newArrayList(1, 2, 3);
        Vector<Integer> v2 = new Vector<>(list);

        Assert.assertThat(v1.size(), is(n));
        Assert.assertThat(v2.size(), is(list.size()));

        for (Integer val : v1) {
            Assert.assertThat(val, is(0));
        }

        for (int i = 0; i < list.size(); i++) {
            Assert.assertThat(list.get(i), is(v2.get(i)));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void initShouldThrow() throws Exception {
        new Vector<>(-1, 0);
    }

    @Test
    public void get() throws Exception {
        List<Integer> ints = Lists.newArrayList(1, 2, 3, 4, 5, 6);
        Vector<Integer> v1 = new Vector<>(ints);

        for (int i = 0; i < ints.size(); i++) {
            Assert.assertThat(ints.get(i), is(v1.get(i)));
        }

        List<Integer> indexes = Lists.newArrayList(1, 2, 3);
        Vector<Integer> slice = v1.get(indexes);

        for (int i = 0; i < indexes.size(); i++) {
            Assert.assertThat(slice.get(i), is(ints.get(indexes.get(i))));
        }

        Vector<Integer> vIndexes = new Vector<>(indexes);
        Vector<Integer> slice2 = v1.get(vIndexes);
        Assert.assertThat(slice, is(slice2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getThrowsNegative() throws Exception {
        List<Integer> ints = Lists.newArrayList(1, 2, 3, 4, 5, 6);
        new Vector<>(ints).get(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getThrowsOutOfBounds() throws Exception {
        List<Integer> ints = Lists.newArrayList(1, 2, 3, 4, 5, 6);
        new Vector<>(ints).get(ints.size());
    }

    @Test
    public void set() throws Exception {
        List<Integer> ints = Lists.newArrayList(1, 2, 3, 4, 5, 6);
        Vector<Integer> v1 = new Vector<>(ints);

        v1.set(0, 10);
        Assert.assertThat(v1.get(0), is(10));
        Assert.assertThat(ints.get(0), not(10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setThrowsNegative() throws Exception {
        List<Integer> ints = Lists.newArrayList(1, 2, 3, 4, 5, 6);
        new Vector<>(ints).set(-1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setThrowsOutOfBounds() throws Exception {
        List<Integer> ints = Lists.newArrayList(1, 2, 3, 4, 5, 6);
        new Vector<>(ints).set(ints.size(), 1);
    }

    @Test
    public void dot() throws Exception {
        Vector<Integer> v1 = new Vector<>(-1, 0, 1);
        Vector<Integer> v2 = new Vector<>(1, 0, 1);

        Double dotProduct = v1.dot(v2);
        Assert.assertThat(dotProduct, is(0d));

        v1 = new Vector<>(1, 2, 3);

        dotProduct = v1.dot(v2);
        Assert.assertThat(dotProduct, is(4d));
    }

    @Test(expected = IllegalArgumentException.class)
    public void dotThrows() throws Exception {
        Vector<Integer> v1 = new Vector<>(1);
        Vector<Integer> v2 = new Vector<>(1, 2, 3);
        v1.dot(v2);
    }

    @Test
    public void plus() throws Exception {
        Vector<Integer> v1 = new Vector<>(-1, 0, 1);
        Vector<Integer> v2 = new Vector<>(1, 0, -1);

        Vector<Double> result = v1.plus(v2);
        Vector<Double> expectedResult = new Vector<>(0d, 0d, 0d);
        Assert.assertThat(result, is(expectedResult));

        v1 = new Vector<>(-1, 0, 1);
        v2 = new Vector<>(1, 0, 2);

        result = v1.plus(v2);
        expectedResult = new Vector<>(0d, 0d, 3d);
        Assert.assertThat(result, is(expectedResult));

        v1 = new Vector<>(0, 0, 0);
        v2 = new Vector<>(0, 0, 0);

        result = v1.plus(v2);
        expectedResult = new Vector<>(0d, 0d, 0d);
        Assert.assertThat(result, is(expectedResult));
    }

    @Test
    public void plusComponentWise() throws Exception {
        Vector<Integer> v1 = new Vector<>(-1, 0, 1);
        Vector<Double> result = v1.plus(2);
        Vector<Double> expectedResult = new Vector<>(1d, 2d, 3d);

        Assert.assertThat(result, is(expectedResult));

        v1 = Vector.ZEROS_3_D;
        result = v1.plus(0);
        expectedResult = Vector.doubleZeros(v1.size());

        Assert.assertThat(result, is(expectedResult));

        result = v1.plus(-10);
        expectedResult = new Vector<>(v1.size(), -10d);
        Assert.assertThat(result, is(expectedResult));
    }

    @Test
    public void minus() throws Exception {
        Vector<Integer> v1 = new Vector<>(-1, 0, 1);
        Vector<Integer> v2 = new Vector<>(1, 0, -1);

        Vector<Double> result = v1.minus(v2);
        Vector<Double> expectedResult = new Vector<>(-2d, 0d, 2d);
        Assert.assertThat(result, is(expectedResult));

        v1 = new Vector<>(-1, 0, 1);
        v2 = new Vector<>(1, 0, 2);

        result = v1.minus(v2);
        expectedResult = new Vector<>(-2d, 0d, -1d);
        Assert.assertThat(result, is(expectedResult));

        v1 = new Vector<>(0, 0, 0);
        v2 = new Vector<>(0, 0, 0);

        result = v1.minus(v2);
        expectedResult = new Vector<>(0d, 0d, 0d);
        Assert.assertThat(result, is(expectedResult));
    }

    @Test
    public void times() throws Exception {
        Vector<Integer> v1 = new Vector<>(-1, 0, 1);
        Vector<Integer> v2 = new Vector<>(1, 0, -1);

        Vector<Double> result = v1.times(v2);
        Vector<Double> expectedResult = new Vector<>(-1d, 0d, -1d);
        Assert.assertThat(result, is(expectedResult));

        result = Vector.ZEROS_3_F.times(Vector.ZEROS_3_F);
        Assert.assertThat(result, is(Vector.ZEROS_3_F));


        result = Vector.ONES_3_F.times(10);
        Assert.assertThat(result, is(new Vector<>(result.size(), 10d)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void timesThrows() throws Exception {
        new Vector<>(1, 2, 3).times(new Vector<>(1));
    }

    @Test
    public void logGamma() throws Exception {
        Vector<Integer> v = new Vector<>(1, 2, 3);
        Vector<Double> result = v.logGamma();

        for (int i = 0; i < v.size(); i++) {
            Assert.assertThat(Maths.logGamma(v.get(i)), is(result.get(i)));
        }
    }

    @Test
    public void logGammaInfinity() {
        Vector<Double> result = Vector.ZEROS_3_F.logGamma();
        for (Double val : result) {
            Assert.assertTrue(val.isInfinite());
        }
    }

    @Test
    public void logGammaNaN() throws Exception {
        Vector<Double> result = new Vector<>(3, -1d).logGamma();
        for (Double val : result) {
            Assert.assertTrue(val.isNaN());
        }
    }

    @Test
    public void log() throws Exception {
        Vector<Integer> v = new Vector<>(1, 2, 3);
        Vector<Double> result = v.log();

        for (int i = 0; i < v.size(); i++) {
            Assert.assertThat(Math.log(v.get(i)), is(result.get(i)));
        }
    }

    @Test
    public void logThrows() {
        Vector<Double> result = Vector.ZEROS_3_F.log();
        for (Double val : result) {
            Assert.assertTrue(val.isInfinite());
        }
    }

    @Test
    public void logNaN() throws Exception {
        Vector<Double> result = new Vector<>(3, -1d).log();
        for (Double val : result) {
            Assert.assertTrue(val.isNaN());
        }
    }

    @Test
    public void exp() throws Exception {
        Vector<Double> result = Vector.ZEROS_3_F.exp();
        Assert.assertThat(result, is(Vector.ONES_3_F));

        result = Vector.ONES_3_F.exp();
        Assert.assertThat(result, is(new Vector<>(3, Math.exp(1))));
        Assert.assertThat(result, is(Vector.exp(Vector.ONES_3_F)));


        result = new Vector<>(1, 2, 3).exp();
        Vector<Double> expectedResult = new Vector<>(Math.exp(1), Math.exp(2), Math.exp(3));
        Assert.assertThat(result, is(expectedResult));
        Assert.assertThat(result, is(Vector.exp(new Vector<>(1, 2, 3))));
    }

    @Test
    public void sum() throws Exception {
        Vector<Integer> v = new Vector<>(0, 0, 0);
        double sum = v.sum();
        Assert.assertThat(sum, is(0d));

        v = new Vector<>(1, 2, 3, 4);
        sum = v.sum();
        Assert.assertThat(sum, is(10d));

        v = new Vector<>(-1, 1, -2, 2);
        sum = v.sum();
        Assert.assertThat(sum, is(0d));
    }

    @Test
    public void max() throws Exception {
        Vector<Integer> v = new Vector<>(0, 0, 0);
        int max = v.max();
        Assert.assertThat(max, is(0));

        v = new Vector<>(0, 1, 2);
        max = v.max();
        Assert.assertThat(max, is(2));

        Vector<Double> vd = new Vector<>(0d, 0d, 0d);
        double dMax = vd.max();
        Assert.assertThat(dMax, is(0d));

        vd = new Vector<>(0d, 1d, 2d);
        dMax = vd.max();
        Assert.assertThat(dMax, is(2d));

        vd = new Vector<>(-2d, -1d);
        dMax = vd.max();
        Assert.assertThat(dMax, is(-1d));

        vd = new Vector<>(0d, 1d, Double.NaN);
        dMax = vd.max();
        Assert.assertThat(dMax, is(Double.NaN));

        vd = new Vector<>(0d, 1d, Double.POSITIVE_INFINITY);
        dMax = vd.max();
        Assert.assertThat(dMax, is(Double.POSITIVE_INFINITY));

        vd = new Vector<>(0d, 1d, Double.NEGATIVE_INFINITY);
        dMax = vd.max();
        Assert.assertThat(dMax, is(1d));
    }

    @Test
    public void asList() throws Exception {
        Vector<Double> v = new Vector<>(0d, 1d, 2d);
        List<Double> doubles = v.asList();
        for (int i = 0; i < v.size(); i++) {
            Assert.assertThat(v.get(i), is(doubles.get(i)));
        }

        v.set(0, 10d);
        Assert.assertThat(v.get(0), not(doubles.get(0)));
    }

    @Test
    public void subVector() throws Exception {
        Vector<Integer> v = new Vector<>(1, 2, 3, 4);
        Vector<Integer> result = v.subVector(2);
        Vector<Integer> expected = new Vector<>(Lists.newArrayList(3, 4));
        Assert.assertThat(result, is(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void subVectorThrowsNegative() throws Exception {
        new Vector<>(1, 2, 3, 4).subVector(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void subVectorThrowsOutOfBounds() throws Exception {
        new Vector<>(1, 2, 3, 4).subVector(10);
    }

    @Test
    public void argmax() throws Exception {
        Vector<Integer> v = new Vector<>(1, 2, 3, 4, 5);
        int argmax = v.argmax();
        Assert.assertThat(argmax, is(4));

        v = new Vector<>(5, 4, 3, 2, 1);
        argmax = v.argmax();
        Assert.assertThat(argmax, is(0));

        v = new Vector<>(1, 4, 5, 2, 1);
        argmax = v.argmax();
        Assert.assertThat(argmax, is(2));
    }
}