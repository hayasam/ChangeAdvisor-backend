package ch.uzh.ifi.seal.changeadvisor.preprocessing;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.MultiThreadedJLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

/**
 * Spell Checker. Tries to apply possible fixes where possible.
 * Created by alex on 17.07.2017.
 */
public class EnglishSpellChecker implements SpellChecker {

    private static final Logger logger = LoggerFactory.getLogger(EnglishSpellChecker.class);

    private static final Language EN_UK = new BritishEnglish();

    private JLanguageTool languageTool;

    public EnglishSpellChecker() {
        languageTool = new MultiThreadedJLanguageTool(EN_UK);
    }

    /**
     * Try to auto-correct the given text using the rules given by {@link BritishEnglish}.
     *
     * @param text text to auto-correct.
     * @return auto-corrected text.
     */
    public String correct(String text) {
        try {
            text = checkAndCorrect(text);
        } catch (IOException e) {
            logger.error("Failed to check text.", e);
        }
        return text;
    }

    private String checkAndCorrect(String text) throws IOException {
        List<RuleMatch> matches = languageTool.check(text);
        ListIterator<RuleMatch> iterator = matches.listIterator();

        StringBuilder correctSentence = new StringBuilder(text);

        int offset = 0;
        while (iterator.hasNext()) {
            RuleMatch match = iterator.next();

            List<String> suggestedReplacements = match.getSuggestedReplacements();

            if (!suggestedReplacements.isEmpty()) {
                String suggestedReplacement = suggestedReplacements.get(0);
                correctSentence.replace(match.getFromPos() - offset, match.getToPos() - offset, suggestedReplacement);
                offset += (match.getToPos() - match.getFromPos() - suggestedReplacement.length());
            } else {
                logger.debug(String.format("Spellchecker found a probable mistake it can't auto correct: %s", match.toString()));
            }
        }

        return correctSentence.toString();
    }
}
