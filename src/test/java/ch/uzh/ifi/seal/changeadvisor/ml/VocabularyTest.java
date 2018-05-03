package ch.uzh.ifi.seal.changeadvisor.ml;

import com.google.common.base.Splitter;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;

/**
 * Created by alex on 24.07.2017.
 */
public class VocabularyTest {

    private List<List<String>> tokensByDocument;

    private List<String> flatTokenList;

    private List<String> tokens1;

    private List<String> tokens2;

    private List<String> tokens3;

    @Before
    public void setUp() throws Exception {
        tokensByDocument = new ArrayList<>();
        String doc1 = "add rearrang complaint etc organ remov";
        String doc2 = "basic websit wire frost result download";
        String doc3 = "music listen power app amp";
        tokens1 = Lists.newArrayList(Splitter.on(" ").split(doc1));
        tokens2 = Lists.newArrayList(Splitter.on(" ").split(doc2));
        tokens3 = Lists.newArrayList(Splitter.on(" ").split(doc3));
        tokensByDocument = Lists.newArrayList(tokens1, tokens2, tokens3);
        flatTokenList = tokensByDocument.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Test
    public void documentToIds() throws Exception {
        Vocabulary vocabulary = new Vocabulary(tokensByDocument);
        List<List<Integer>> documents = vocabulary.getDocumentIds();

        Assert.assertThat(documents.size(), is(tokensByDocument.size()));
        Assert.assertThat(documents.get(0).size(), is(tokens1.size()));
        Assert.assertThat(documents.get(1).size(), is(tokens2.size()));
        Assert.assertThat(documents.get(2).size(), is(tokens3.size()));
    }

    @Test
    public void vocabsTest() throws Exception {
        Vocabulary vocabulary = new Vocabulary(tokensByDocument);
        List<String> vocabs = vocabulary.getVocabs();

        Assert.assertThat(vocabs.size(), is(tokens1.size() + tokens2.size() + tokens3.size()));
        for (String token : flatTokenList) {
            Assert.assertTrue(vocabs.contains(token));
        }
    }

    @Test
    public void idsTest() throws Exception {
        Vocabulary vocabulary = new Vocabulary(tokensByDocument);
        Map<String, Integer> ids = vocabulary.getTokenIds();

        Assert.assertThat(ids.size(), is(flatTokenList.size()));
    }

    @Test
    public void testAll() throws Exception {
        Vocabulary vocabulary = new Vocabulary(tokensByDocument);
        Map<String, Integer> ids = vocabulary.getTokenIds();
        List<List<Integer>> documents = vocabulary.getDocumentIds();

        assertTokens(tokens1, ids, documents.get(0));
        assertTokens(tokens2, ids, documents.get(1));
        assertTokens(tokens3, ids, documents.get(2));
    }

    private void assertTokens(List<String> tokens, Map<String, Integer> ids, List<Integer> document) {
        for (String token : tokens) {
            Integer id = ids.get(token);
            Assert.assertTrue(document.contains(id));
        }
    }
}