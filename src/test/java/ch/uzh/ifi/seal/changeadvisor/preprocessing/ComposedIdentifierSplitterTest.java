package ch.uzh.ifi.seal.changeadvisor.preprocessing;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class ComposedIdentifierSplitterTest {

    @Test
    public void split() {
        String test = "CamelCase camelCase snake_case digit1digit";
        String split = new ComposedIdentifierSplitter().split(test);
        Assert.assertThat(split, is("Camel Case camel Case snake case digit 1 digit"));

    }
}