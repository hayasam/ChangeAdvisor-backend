package ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.core.Is.is;

public class AsymmetricDiceIndexTest {

    private final AsymmetricDiceIndex asymmetricDiceIndex = new AsymmetricDiceIndex();

    @Test
    public void similarity() throws Exception {
        Set<String> doc1 = Sets.newHashSet("foo", "bar", "baz", "hello", "world");
        Set<String> doc2 = Sets.newHashSet("foo", "bar", "zzz", "www", "world");
        Set<String> doc3 = Sets.newHashSet("aa", "bb", "zzz", "www", "cc");
        Set<String> doc4 = Sets.newHashSet("aa", "bb", "zzz", "hello", "cc");
        Set<String> doc5 = IntStream.range(0, 100).mapToObj(i -> UUID.randomUUID().toString()).collect(Collectors.toSet());
        doc5.add("foo");


        double similarity = asymmetricDiceIndex.similarity(doc1, doc2);

        Assert.assertThat(similarity, is(1.0));

        similarity = asymmetricDiceIndex.similarity(doc2, doc3);
        Assert.assertThat(similarity, is(0.8));

        similarity = asymmetricDiceIndex.similarity(doc2, doc4);
        Assert.assertThat(similarity, is(0.4));

        similarity = asymmetricDiceIndex.similarity(doc1, doc5);
        Assert.assertThat(similarity, is(0.4));
    }

    @Test
    public void noSimilarity() throws Exception {
        Set<String> doc1 = Sets.newHashSet("foo", "bar", "baz", "hello", "world");
        Set<String> doc2 = Sets.newHashSet("aa", "bb", "zzz", "www", "cc");
        Set<String> doc3 = IntStream.range(0, 99).mapToObj(i -> UUID.randomUUID().toString()).collect(Collectors.toSet());
        Set<String> doc4 = IntStream.range(0, 99).mapToObj(i -> UUID.randomUUID().toString()).collect(Collectors.toSet());


        double similarity = asymmetricDiceIndex.similarity(doc1, doc2);
        Assert.assertThat(similarity, is(0.0));

        similarity = asymmetricDiceIndex.similarity(doc1, doc4);
        Assert.assertThat(similarity, is(0.0));

        similarity = asymmetricDiceIndex.similarity(doc3, doc4);
        Assert.assertThat(similarity, is(0.0));
    }

    @Test
    public void test() throws Exception {
        Set<String> doc5 = Sets.newHashSet(Splitter.on(" ").omitEmptyStrings().trimResults().splitToList("play parent disconnect select prefix mime visibl type compon music context action text soft creat posit item method scroll index manag click input system util reset notifi subtitl artist icon overrid search default interfac audio close connect resourc break restart column store menu cach token filter fanci hide inflat throw grid paus return loader project extern fling listen view stub finish titl binder info void profil swap adapt count touch list fals servic disk null true name final option datum cursor string submit pars android focu medium content pollo switch total public queri bundl move album encod home equal super boolean chang inherit window base empti"));
        Set<String> doc6 = Sets.newHashSet(Splitter.on(" ").omitEmptyStrings().trimResults().splitToList("allow play minut datum instal rerout quick wait loader pleas screen polici mobil download search onald farm freez connect song coupl wife night fault click lack home market exit phone chang reset time page"));
        Set<String> doc7 = Sets.newHashSet(Splitter.on(" ").omitEmptyStrings().trimResults().splitToList("datum lack connect wife mobil home rerout page click wait freez phone exit minut screen search reset time instal farm onald night play quick song coupl download polici allow chang pleas fault loader market "));

        double similarity = asymmetricDiceIndex.similarity(doc5, doc7);
        Assert.assertEquals(0.529411765, similarity, 0.001);
    }
}
