package ch.uzh.ifi.seal.changeadvisor.tfidf;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;

public class TFiDFTest {

    private List<AbstractNGram> tokens1 = Lists.newArrayList("this", "is", "a", "a", "sample")
            .stream().map(Unigram::new).collect(Collectors.toList());

    private List<AbstractNGram> tokens2 = Lists.newArrayList("this", "is", "another", "another", "example", "example", "example")
            .stream().map(Unigram::new).collect(Collectors.toList());

    private Document d1 = new Document(tokens1);

    private Document d2 = new Document(tokens2);

    @Test
    public void frequency() throws Exception {
        Document emptyDocument = new Document(Lists.newArrayList());

        Unigram thisUnigram = new Unigram("this");
        Unigram exampleUnigram = new Unigram("example");

        TFiDF tFiDF = new TFiDF();
        double frequency = tFiDF.tf(thisUnigram, d1);
        Assert.assertThat(frequency, is(1. / 5));

        frequency = tFiDF.tf(thisUnigram, d2);
        Assert.assertThat(frequency, is(1. / 7));

        frequency = tFiDF.tf(thisUnigram, emptyDocument);
        Assert.assertThat(frequency, is(0.0));

        frequency = tFiDF.tf(exampleUnigram, d1);
        Assert.assertThat(frequency, is(0.0));

        frequency = tFiDF.tf(exampleUnigram, d2);
        Assert.assertThat(frequency, is(3. / 7));
    }

    @Test
    public void idf() throws Exception {
        Corpus corpus = new Corpus(Lists.newArrayList(d1, d2));

        Unigram thisUnigram = new Unigram("this");
        Unigram exampleUnigram = new Unigram("example");

        TFiDF tFiDF = new TFiDF();
        double result = tFiDF.idf(thisUnigram, corpus);
        Assert.assertThat(result, is(0.0));

        result = tFiDF.idf(exampleUnigram, corpus);
        Assert.assertThat(result, is(Math.log10(2)));
    }

    @Test
    public void computeTfidf() throws Exception {
        Corpus corpus = new Corpus(Lists.newArrayList(d1, d2));

        Unigram exampleUnigram = new Unigram("example");

        TFiDF tFiDF = new TFiDF();
        double result = tFiDF.compute(exampleUnigram, d1, corpus);
        Assert.assertThat(result, is(0.0));

        result = tFiDF.compute(exampleUnigram, d2, corpus);
        Assert.assertThat(result, is(Math.log10(2) * 3. / 7));

        // word not in corpus.
        Unigram random = new Unigram("asdfasdf");
        result = tFiDF.compute(random, d1, corpus);
        Assert.assertThat(result, is(0.0));
        result = tFiDF.compute(random, d2, corpus);
        Assert.assertThat(result, is(0.0));
    }
}