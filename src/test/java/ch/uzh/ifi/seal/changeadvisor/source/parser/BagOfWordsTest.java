package ch.uzh.ifi.seal.changeadvisor.source.parser;

import ch.uzh.ifi.seal.changeadvisor.preprocessing.CorpusProcessor;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElement;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.google.common.base.Splitter;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by alex on 14.07.2017.
 */
public class BagOfWordsTest {

    private static final String TEST_DIRECTORY = "test_files_parser";
    private static final String TEST_FROSTWIRE_APP = "com.frostwire.android";

    @Test
    public void frostwireClass() throws Exception {
        String bagOfWordFromPoC = "Constructor code Album Adapter param context link Context layout resource view inflate style Determines which therefore items load public final Activity super Layout Image Fetcher Apollo Utils Overlay Resources Color color list background inherit Override View position convert Group parent Music Holder holder null Inflater from false else Data data Line Text Item Load Extra Background Three Artist Touch Play play return boolean Stable true Type Count VIEW TYPE COUNT Method used cache populate grid idea everything before called void build Cache album Name make Label plurals Nsongs Song Number Starts playing user touches artwork holding private Click Listener long that unloads clears adapter pause True temporarily disk otherwise Pause Disk find cached remove From generate Flushes flush extra line three image when touched";
        List<String> split = new ArrayList<>(Splitter.on(' ').trimResults().omitEmptyStrings().splitToList(bagOfWordFromPoC));
        Collections.sort(split);

        Path path = Paths.get(TEST_DIRECTORY + "/" + TEST_FROSTWIRE_APP + "/android/apollo/src/com/andrew/apollo/adapters/AlbumAdapter.java");
        CompilationUnit cu = JavaParser.parse(path);
        String packageName = cu.getPackageDeclaration().get().getName().toString();

        String corpus = FileUtils.readFileToString(path.toFile(), "utf-8");

        CorpusProcessor processor = new CorpusProcessor.Builder()
                .escapeSpecialChars()
                .withComposedIdentifierSplit()
//                .withAutoCorrect(new EnglishSpellChecker()) // Warning huge performance impact
                .singularize()
                .lowerCase()
                .removeStopWords()
                .stem()
                .removeTokensShorterThan(3)
                .removeDuplicates(true)
                .build();
        Collection<String> bag = processor.process(corpus);
        CodeElement bagOfWords = new CodeElement("", packageName + "." + path.getFileName().toString(), bag);

        bagOfWords.writeToFile(Paths.get(TEST_DIRECTORY + "/test_generated/processed_source_components.csv"), false);

        List<String> orderedBagOfWords = bagOfWords.getSortedBag();

        Assert.assertEquals(137, orderedBagOfWords.size());
    }
}
