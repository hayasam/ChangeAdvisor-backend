package ch.uzh.ifi.seal.changeadvisor.preprocessing;

import java.util.regex.Pattern;

/**
 * Splits composed identifies (e.g. CamelCase, snake_case, and digit separated text) into tokens.
 * Created by alex on 14.07.2017.
 */
public class ComposedIdentifierSplitter {

    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile(String.format("%s|%s|%s",
            "(?<=[A-Z])(?=[A-Z][a-z])",
            "(?<=[^A-Z])(?=[A-Z])",
            "(?<=[A-Za-z])(?=[^A-Za-z])"
    ));

    private static final Pattern DIGIT_SEPARATED_TEXT_PATTERN = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s\\s\\s*");

    public String split(String text) {
        text = splitCamelCase(text);
        text = splitUnderScoreText(text);
        text = splitDigitSeparatedText(text);
        text = cleanupExtraWhiteSpaces(text);
        return text;
    }

    private String splitCamelCase(String s) {
        return CAMEL_CASE_PATTERN.matcher(s).replaceAll(" ");
    }

    private String splitUnderScoreText(String s) {
        return s.replace('_', ' ');
    }

    private String splitDigitSeparatedText(String s) {
        return DIGIT_SEPARATED_TEXT_PATTERN.matcher(s).replaceAll(" ");
    }

    private String cleanupExtraWhiteSpaces(String s) {
        return WHITESPACE_PATTERN.matcher(s).replaceAll(" ");
    }
}
