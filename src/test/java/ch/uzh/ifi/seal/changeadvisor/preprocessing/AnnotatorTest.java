package ch.uzh.ifi.seal.changeadvisor.preprocessing;

import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;

/**
 * Created by alex on 20.07.2017.
 */
public class AnnotatorTest {

    private Annotator annotator = new Annotator();

    @Test
    public void filter() throws Exception {
        final List<String> nounsAndVerbs = Lists.newArrayList(
                "Add", "text", "can", "contain", "sentences", "'s", "pushing", "edge", "'ve", "was", "thing", "had", "seen", "do");

        Collection<AnnotatedToken> annotatedTokens = annotator.annotate("Add your text here! It can contain multiple sentences. It's pushing me over the longest edge I've ever seen it was the evilest thing I had ever seen him do!", true, true);
        List<String> filtered = annotatedTokens.stream().map(AnnotatedToken::getToken).collect(Collectors.toList());
        Collections.sort(filtered);
        Collections.sort(nounsAndVerbs);

        Assert.assertThat(nounsAndVerbs.size(), is(filtered.size()));
        for (String token : filtered) {
            Assert.assertTrue(nounsAndVerbs.contains(token));
        }
    }

    @Test
    public void filterEmpty() throws Exception {
        Collection<AnnotatedToken> filter = annotator.annotate(null, true, true);
        Assert.assertTrue(filter.isEmpty());

        filter = annotator.annotate("", true, true);
        Assert.assertTrue(filter.isEmpty());
    }
}