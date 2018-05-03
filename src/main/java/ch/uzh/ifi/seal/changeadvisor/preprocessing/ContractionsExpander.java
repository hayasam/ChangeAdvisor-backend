package ch.uzh.ifi.seal.changeadvisor.preprocessing;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Expands contractions in the english language.
 * Created by alex on 17.07.2017.
 */
public class ContractionsExpander {

    private static final String TWO_PARTS_CONTRACTION_REGEXP = "(\\w*)\\s*'\\s*(\\w+)";
    private static final String THREE_PARTS_CONTRACTION_REGEXP = "(\\w+)\\s*'\\s*(\\w+)\\s*'\\s*(\\w+)";

    private static final Pattern TWO_PARTS_CONTRACTION_PATTERN = Pattern.compile(TWO_PARTS_CONTRACTION_REGEXP);
    private static final Pattern THREE_PARTS_CONTRACTION_PATTERN = Pattern.compile(THREE_PARTS_CONTRACTION_REGEXP);

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
                    .put("you've", "you have")
                    .build();

    boolean isTwoPartsContraction(String text) {
        return TWO_PARTS_CONTRACTION_PATTERN.matcher(text).matches();
    }

    boolean isThreePartsContraction(String text) {
        return THREE_PARTS_CONTRACTION_PATTERN.matcher(text).matches();
    }

    public String expand(String text) {
        if (!StringUtils.isEmpty(text)) {
            Matcher matcher = TWO_PARTS_CONTRACTION_PATTERN.matcher(text);
            List<String> twoPartsMatches = new ArrayList<>();
            while (matcher.find()) {
                String match = matcher.group(0);
                twoPartsMatches.add(match);
            }

            matcher = THREE_PARTS_CONTRACTION_PATTERN.matcher(text);
            List<String> threePartsMatches = new ArrayList<>();
            while (matcher.find()) {
                threePartsMatches.add(matcher.group(0));
            }

            text = replaceMatches(text, twoPartsMatches);
            text = replaceMatches(text, threePartsMatches);
        } else {
            text = "";
        }
        return text;
    }

    private String replaceMatches(String corpus, List<String> matches) {
        for (String match : matches) {
            corpus = corpus.replace(match, contractions.getOrDefault(match.toLowerCase(), match));
        }
        return corpus;
    }
}
