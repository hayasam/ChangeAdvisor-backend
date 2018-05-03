package ch.uzh.ifi.seal.changeadvisor;

import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;

import java.util.List;

/**
 * Created by alex on 17.07.2017.
 */
public class JLanguageToolTest {

    @Test
    public void spellChecking() throws Exception {
        JLanguageTool langTool = new JLanguageTool(new BritishEnglish());

        final String sentence = "A sentence with a error in the Hitchhiker's Guide tot he Galaxy";
        List<RuleMatch> matches = langTool.check(sentence);
        for (RuleMatch match : matches) {
            System.out.println("Potential error at characters " +
                    match.getFromPos() + "-" + match.getToPos() + ": " +
                    match.getMessage());
            System.out.println("Suggested correction(s): " +
                    match.getSuggestedReplacements());
        }

        System.out.println("Printing corrected versions:");
        for (RuleMatch match : matches) {
            List<String> suggestedReplacements = match.getSuggestedReplacements();
            int fromPos = match.getFromPos();
            int toPos = match.getToPos();
            for (String suggestion : suggestedReplacements) {
                String correction = sentence.substring(0, fromPos) + suggestion + sentence.substring(toPos);
                System.out.println(correction);
            }
        }
    }
}
