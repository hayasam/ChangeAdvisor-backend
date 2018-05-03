package ch.uzh.ifi.seal.changeadvisor.ml.math;

import cc.mallet.util.Maths;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a vector of arbitrary size.
 * Simplifies vector operations in ML code.
 *
 * @param <T> Concrete Number implementation.
 * @see Number
 */
public class Vector<T extends Number> implements Iterable<T> {

    static final Vector<Integer> ZEROS_3_D = intZeros(3);

    static final Vector<Double> ZEROS_3_F = doubleZeros(3);

    static final Vector<Double> ONES_3_F = new Vector<>(3, 1d);

    /**
     * Inner list to contain values.
     * Note: maybe change with arrays later on for improved performance?
     */
    private List<T> v;

    /**
     * Vector size.
     */
    private final int n;

    /**
     * Constructs a vector instance where all elements are initialized with a default value.
     *
     * @param n            size of vector.
     * @param defaultValue default value.
     */
    public Vector(int n, T defaultValue) {
        if (n < 0) {
            throw new IllegalArgumentException(String.format("Illegal Capacity: %d", n));
        }
        this.n = n;
        this.v = new ArrayList<>(n);
        init(defaultValue);
    }

    /**
     * Constructs a vector using the given list to populate itself.
     * Note: it makes a defensive copy of the list, so the list is still safe to use afterwards.
     *
     * @param values items to use to populate vector.
     */
    public Vector(List<T> values) {
        assert values != null;
        this.n = values.size();
        this.v = new ArrayList<>(values);
    }

    /**
     * Constructs a vector using the given list to populate itself.
     * Note: it makes a defensive copy of the list, so the list is still safe to use afterwards.
     *
     * @param values items to use to populate vector.
     * @see Vector#Vector(List)
     */
    public Vector(T... values) {
        assert values != null && values.length > 0;

        this.n = values.length;
        this.v = new ArrayList<>(n);
        v.addAll(Arrays.asList(values));
    }

    /**
     * Initializes vector with a default value.
     *
     * @param defaultValue default value.
     */
    private void init(T defaultValue) {
        for (int i = 0; i < n; i++) {
            v.add(defaultValue);
        }
    }

    /**
     * Returns a copy of this vector as list.
     * The returned list is a deep copy and is safe for continued use.
     *
     * @return vector as list.
     * @see Vector#asList()
     */
    public List<T> get() {
        return new ArrayList<>(v);
    }

    /**
     * Returns an item from this vector.
     *
     * @param i index.
     * @return item at index i.
     */
    public T get(int i) {
        assertDimension(i);
        return v.get(i);
    }

    /**
     * Gets a subvector from this vector by retrieving only the items given from ids.
     *
     * @param ids index list.
     * @return list composed of items at indexes in ids as a new vector.
     */
    public Vector<T> get(Vector<Integer> ids) {
        return get(ids.v);
    }

    /**
     * @see Vector#get(List)
     */
    public Vector<T> get(List<Integer> ids) {
        return new Vector<>(ids.stream().map(v::get).collect(Collectors.toList()));
    }

    /**
     * Replaces item at i with a new value.
     *
     * @param i     index.
     * @param value new value.
     */
    public void set(int i, T value) {
        assertDimension(i);
        v.set(i, value);
    }

    /**
     * Computes the dot product between this vector and another.
     *
     * @param other the other vector.
     * @param <S>   type of other vector.
     * @return dot product between this and other.
     */
    public <S extends Number> Double dot(Vector<S> other) {
        assertSameDimensions(other);
        double result = 0;
        for (int i = 0; i < n; i++) {
            result += v.get(i).doubleValue() * other.v.get(i).doubleValue();
        }
        return result;
    }

    /**
     * Computes the sum of this vector and another vector.
     *
     * @param other the other vector.
     * @return component-wise sum.
     */
    public Vector<Double> plus(Vector<T> other) {
        assertSameDimensions(other);
        List<Double> vResult = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            vResult.add(v.get(i).doubleValue() + other.v.get(i).doubleValue());
        }

        return new Vector<>(vResult);
    }

    /**
     * Adds to each component of this vector a given value.
     *
     * @param val value to add.
     * @return vector-scalar sum.
     */
    public Vector<Double> plus(double val) {
        List<Double> result = v.stream().map(i -> i.doubleValue() + val).collect(Collectors.toList());
        return new Vector<>(result);
    }

    /**
     * Computes the subtraction of this vector and another vector.
     *
     * @param other the other vector.
     * @return component-wise subtraction.
     */
    public Vector<Double> minus(Vector<T> other) {
        assertSameDimensions(other);
        List<Double> vResult = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            vResult.add(v.get(i).doubleValue() - other.v.get(i).doubleValue());
        }

        return new Vector<>(vResult);
    }

    /**
     * Computes the component-wise multiplication of this vector and another vector.
     *
     * @param other the other vector.
     * @return component-wise multiplication.
     */
    public <S extends Number> Vector<Double> times(Vector<S> other) {
        assertSameDimensions(other);
        List<Double> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            result.add(v.get(i).doubleValue() * other.v.get(i).doubleValue());
        }
        return new Vector<>(result);
    }

    /**
     * Computes the vector-scalar multiplication.
     *
     * @param mult value to multiply by.
     * @param <S>  type of scalar.
     * @return vector-scalar multiplication result.
     */
    public <S extends Number> Vector<Double> times(S mult) {
        List<Double> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            result.add(v.get(i).doubleValue() * mult.doubleValue());
        }
        return new Vector<>(result);
    }

    /**
     * Computes the vector-scalar division.
     *
     * @param div value to divide by.
     * @param <S> type of scalar.
     * @return vector-scalar division result.
     * @see Vector#times(Number)
     */
    public <S extends Number> Vector<Double> dividedBy(S div) {
        return times(1d / div.doubleValue());
    }

    /**
     * Computes log gamma on each component of this vector.
     *
     * @return vector containing the result of computing log-gamma on each item.
     * @see Maths#logGamma(double)
     */
    public Vector<Double> logGamma() {
        List<Double> result = v.stream().map(x -> Maths.logGamma(x.doubleValue())).collect(Collectors.toList());
        return new Vector<>(result);
    }

    /**
     * Computes log gamma on each component of this vector.
     *
     * @return vector containing the result of computing log on each item.
     * @see Math#log(double).
     */
    public Vector<Double> log() {
        List<Double> result = v.stream().map(x -> Math.log(x.doubleValue())).collect(Collectors.toList());
        return new Vector<>(result);
    }

    /**
     * Computes exp on each component of this vector.
     *
     * @return vector containing the result of computing exp on each item.
     * @see Math#exp(double).
     */
    public Vector<Double> exp() {
        List<Double> result = v.stream().map((T a) -> Math.exp(a.doubleValue())).collect(Collectors.toList());
        return new Vector<>(result);
    }

    /**
     * Computes exp on each component of this vector.
     *
     * @return vector containing the result of computing exp on each item.
     * @see Vector#exp().
     */
    public static <T extends Number> Vector<Double> exp(Vector<T> vector) {
        return vector.exp();
    }

    /**
     * Computes the result of adding each item in this vector.
     *
     * @return sum result.
     */
    public double sum() {
        return v.stream().mapToDouble(Number::doubleValue).sum();
    }

    /**
     * Finds the max value in this vector.
     *
     * @return max component in vector.
     */
    public T max() {
        Optional<T> max = v.stream().max(Comparator.comparingDouble(Number::doubleValue));
        return max.orElseThrow(() -> new IllegalArgumentException("Cannot compute max of empty vector!"));
    }

    private void assertDimension(int i) {
        if (i >= n || i < 0) {
            throw new IllegalArgumentException(String.format("Vector dimension mismatch, valid range: (%d); got: (%d)", n, i));
        }
    }

    private <S extends Number> void assertSameDimensions(Vector<S> other) {
        if (n != other.size()) {
            throw new IllegalArgumentException(String.format("Vector dimension mismatch: this(%d), other(%d)", n, other.size()));
        }
    }

    /**
     * Size of vector.
     *
     * @return size.
     */
    public int size() {
        return n;
    }

    public int argmax() {
        T max = max();
        return v.indexOf(max);
    }

    public Vector<T> subVector(int fromIndex) {
        assertDimension(fromIndex);
        return new Vector<>(v.subList(fromIndex, v.size()));
    }

    /**
     * Returns a copy of this vector as list.
     * The returned list is a deep copy and is safe for continued use.
     *
     * @return vector as list.
     * @see Vector#asList()
     */
    public List<T> asList() {
        return get();
    }

    @Override
    public String toString() {
        return String.format("{%d}: %s", n, v.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector<?> vector = (Vector<?>) o;

        if (n != vector.n) return false;
        return v != null ? v.equals(vector.v) : vector.v == null;
    }

    @Override
    public int hashCode() {
        int result = v != null ? v.hashCode() : 0;
        result = 31 * result + n;
        return result;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return v.iterator();
    }

    public static Vector<Integer> intZeros(int n) {
        return new Vector<>(n, 0);
    }

    public static Vector<Double> doubleZeros(int n) {
        return new Vector<>(n, 0d);
    }

    public Vector<T> copy() {
        return new Vector<>(v);
    }

    public static Vector<Double> toDoubleVector(List<? extends Number> list) {
        return new Vector<>(list.stream().map(Number::doubleValue).collect(Collectors.toList()));
    }
}
