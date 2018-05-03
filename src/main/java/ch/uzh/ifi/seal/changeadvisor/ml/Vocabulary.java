package ch.uzh.ifi.seal.changeadvisor.ml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alex on 24.07.2017.
 */
public class Vocabulary {

    /**
     * Flat token list.
     */
    private List<String> vocabs;

    /**
     * Token -> "id" map. id is the insertion order of each token.
     */
    private Map<String, Integer> tokenIds;

    /**
     * Document ids.
     */
    private List<List<Integer>> documentIds;

    public Vocabulary(Corpus corpus) {
        this(corpus.getDocuments());
    }

    /**
     * @param documents list of document tokens (Tokens by document).
     */
    public Vocabulary(List<List<String>> documents) {
        int initSize = getInitialSize(documents);
        vocabs = new ArrayList<>(initSize);
        tokenIds = new HashMap<>(initSize);
        documentIds = new ArrayList<>(initSize);

        for (List<String> document : documents) {
            List<Integer> ids = documentToIds(document);
            documentIds.add(ids);
        }
    }

    private int getInitialSize(List<List<String>> documents) {
        int docs = documents.size();
        int size = 0;
        if (!documents.isEmpty()) {
            size = documents.get(0).size();
        }
        return docs * size;
    }

    private List<Integer> documentToIds(List<String> document) {
        List<Integer> ids = new ArrayList<>();
        for (String token : document) {
            ids.add(vocabToId(token));
        }
        return ids;

    }

    private int vocabToId(String token) {
        if (!tokenIds.containsKey(token)) {
            int id = vocabs.size();
            tokenIds.put(token, id);
            vocabs.add(token);
            return id;
        }
        return tokenIds.get(token);
    }

    public List<String> getVocabs() {
        return vocabs;
    }

    public Map<String, Integer> getTokenIds() {
        return tokenIds;
    }

    public List<List<Integer>> getDocumentIds() {
        return documentIds;
    }

    public int vocabularySize() {
        return vocabs.size();
    }

    public String getVocab(int i) {
        return vocabs.get(i);
    }
}
