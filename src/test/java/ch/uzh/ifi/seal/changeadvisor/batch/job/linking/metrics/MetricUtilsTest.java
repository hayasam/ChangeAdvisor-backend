package ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;

public class MetricUtilsTest {

    private final MetricUtils metricUtils = new MetricUtils();

    @Test
    public void countEmptySet() throws Exception {
        Set<String> doc = Sets.newHashSet("foo", "bar", "baz", "hello", "world");

        int i = metricUtils.countOverlappingWords(doc, new HashSet<>());
        Assert.assertThat(i, is(0));

        i = metricUtils.countOverlappingWords(new HashSet<>(), doc);
        Assert.assertThat(i, is(0));

        i = metricUtils.countOverlappingWords(new HashSet<>(), new HashSet<>());
        Assert.assertThat(i, is(0));
    }

    @Test
    public void countOverlappingWords() throws Exception {
        Set<String> doc1 = Sets.newHashSet("foo", "bar", "baz", "hello", "world");
        Set<String> doc2 = Sets.newHashSet("foo", "bar", "zzz", "www", "world");
        Set<String> doc3 = Sets.newHashSet("aa", "bb", "zzz", "www", "cc");

        int i = metricUtils.countOverlappingWords(doc1, doc2);
        Assert.assertThat(i, is(3));

        i = metricUtils.countOverlappingWords(doc1, doc3);
        Assert.assertThat(i, is(0));
    }

    @Test
    public void countOverlappingWords2() throws Exception {
        List<String> doc1 = Lists.newArrayList("foo", "bar", "baz", "hello", "world");
        List<String> doc2 = Lists.newArrayList("foo", "bar", "foo", "www", "world");

        int i = metricUtils.countOverlappingWords(doc1, doc2);
        Assert.assertThat(i, is(4));
    }

    @Test
    public void countSameSet() throws Exception {
        Set<String> doc = Sets.newHashSet("foo", "bar", "baz", "hello", "world");
        int i = metricUtils.countOverlappingWords(doc, doc);
        Assert.assertThat(i, is(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void countNullDoc1() throws Exception {
        metricUtils.countOverlappingWords(null, new HashSet<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void countNullDoc2() throws Exception {
        metricUtils.countOverlappingWords(new HashSet<>(), null);
    }
}
