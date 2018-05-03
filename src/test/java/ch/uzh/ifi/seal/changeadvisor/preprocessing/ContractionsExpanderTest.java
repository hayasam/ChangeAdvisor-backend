package ch.uzh.ifi.seal.changeadvisor.preprocessing;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.hamcrest.core.Is.is;

/**
 * Created by alex on 17.07.2017.
 */
public class ContractionsExpanderTest {

    private static final Logger logger = LoggerFactory.getLogger(ContractionsExpanderTest.class);

    private Map<String, String> contractions =
            ImmutableMap.<String, String>builder()
                    .put("ain't", "am not")
                    .put("aren't", "are not")
                    .put("can't", "cannot")
                    .put("can't've", "cannot have")
                    .put("'cause", "because")
                    .put("could've", "could have")
                    .put("couldn't", "could not")
                    .put("couldn't've", "could not have")
                    .put("didn't", "did not")
                    .put("doesn't", "does not")
                    .put("don't", "do not")
                    .put("hadn't", "had not")
                    .put("hadn't've", "had not have")
                    .put("hasn't", "has not")
                    .put("haven't", "have not")
                    .put("he'd", "he had")
                    .put("he'd've", "he would have")
                    .put("he'll", "he will")
                    .put("he'll've", "he will have")
                    .put("he's", "he is")
                    .put("how'd", "how did")
                    .put("how'd'y", "how do you")
                    .put("how'll", "how will")
                    .put("how's", "how is")
                    .put("i'd", "i would")
                    .put("i'd've", "i would have")
                    .put("i'll", "i will")
                    .put("i'll've", "i will have")
                    .put("i'm", "i am")
                    .put("i've", "i have")
                    .put("isn't", "is not")
                    .put("it'd", "it would")
                    .put("it'd've", "it would have")
                    .put("it'll", "it will")
                    .put("it'll've", "it will have")
                    .put("it's", "it is")
                    .put("let's", "let us")
                    .put("ma'am", "madam")
                    .put("mayn't", "may not")
                    .put("might've", "might have")
                    .put("mightn't", "might not")
                    .put("mightn't've", "might not have")
                    .put("must've", "must have")
                    .put("mustn't", "must not")
                    .put("mustn't've", "must not have")
                    .put("needn't", "need not")
                    .put("needn't've", "need not have")
                    .put("o'clock", "of the clock")
                    .put("oughtn't", "ought not")
                    .put("oughtn't've", "ought not have")
                    .put("shan't", "shall not")
                    .put("sha'n't", "shall not")
                    .put("shan't've", "shall not have")
                    .put("she'd", "she would")
                    .put("she'd've", "she would have")
                    .put("she'll", "she will")
                    .put("she'll've", "she will have")
                    .put("she's", "she is")
                    .put("should've", "should have")
                    .put("shouldn't", "should not")
                    .put("shouldn't've", "should not have")
                    .put("so've", "so have")
                    .put("so's", "so is")
                    .put("that'd", "that would")
                    .put("that'd've", "that would have")
                    .put("that's", "that is")
                    .put("there'd", "there would")
                    .put("there'd've", "there would have")
                    .put("there's", "there is")
                    .put("they'd", "they would")
                    .put("they'd've", "they would have")
                    .put("they'll", "they will")
                    .put("they'll've", "they will have")
                    .put("they're", "they are")
                    .put("they've", "they have")
                    .put("to've", "to have")
                    .put("wasn't", "was not")
                    .put("we'd", "we would")
                    .put("we'd've", "we would have")
                    .put("we'll", "we will")
                    .put("we'll've", "we will have")
                    .put("we're", "we are")
                    .put("we've", "we have")
                    .put("weren't", "were not")
                    .put("what'll", "what will")
                    .put("what'll've", "what will have")
                    .put("what're", "what are")
                    .put("what's", "what is")
                    .put("what've", "what have")
                    .put("when's", "when is")
                    .put("when've", "when have")
                    .put("where'd", "where did")
                    .put("where's", "where is")
                    .put("where've", "where have")
                    .put("who'll", "who will")
                    .put("who'll've", "who will have")
                    .put("who's", "who is")
                    .put("who've", "who have")
                    .put("why's", "why is")
                    .put("why've", "why have")
                    .put("will've", "will have")
                    .put("won't", "will not")
                    .put("won't've", "will not have")
                    .put("would've", "would have")
                    .put("wouldn't", "would not")
                    .put("wouldn't've", "would not have")
                    .put("y'all", "you all")
                    .put("y'all'd", "you all would")
                    .put("y'all'd've", "you all would have")
                    .put("y'all're", "you all are")
                    .put("y'all've", "you all have")
                    .put("you'd", "you would")
                    .put("you'd've", "you would have")
                    .put("you'll", "you will")
                    .put("you'll've", "you will have")
                    .put("you're", "you are")
                    .put("you've", "you have").build();

    @Test
    public void isTwoPartsContraction() throws Exception {
        ContractionsExpander contractionsExpander = new ContractionsExpander();
        for (Map.Entry<String, String> entry : contractions.entrySet()) {
            long count = entry.getKey().chars().filter(ch -> ch == '\'').count();
            if (count == 1) {
                logger.info(entry.getKey());
                Assert.assertTrue(contractionsExpander.isTwoPartsContraction(entry.getKey()));
            }
        }
    }

    @Test
    public void isThreePartsContraction() throws Exception {
        ContractionsExpander contractionsExpander = new ContractionsExpander();
        for (Map.Entry<String, String> entry : contractions.entrySet()) {
            long count = entry.getKey().chars().filter(ch -> ch == '\'').count();
            if (count == 2) {
                Assert.assertTrue(contractionsExpander.isThreePartsContraction(entry.getKey()));
            }
        }
    }

    @Test
    public void contractionExpansion() throws Exception {
        final String s = "This is completely normal text, although the following token is not a contraction: abc's, this one is: haven't";
        final String expected = "This is completely normal text, although the following token is not a contraction: abc's, this one is: have not";
        final String noContras = "This text does not even contain one contraction!";
        final String threePartContras = "I mean... does this even mean anything at all??? 'we'll've'";
        final String threePartContrasExpected = "I mean... does this even mean anything at all??? 'we will have'";

        ContractionsExpander contractionsExpander = new ContractionsExpander();

        String expand = contractionsExpander.expand(s);
        Assert.assertThat(expand, is(expected));

        expand = contractionsExpander.expand(noContras);
        Assert.assertThat(expand, is(noContras));

        expand = contractionsExpander.expand(threePartContras);
        Assert.assertThat(expand, is(threePartContrasExpected));

        expand = contractionsExpander.expand("");
        Assert.assertThat(expand, is(""));

        expand = contractionsExpander.expand(null);
        Assert.assertThat(expand, is(""));
    }
}