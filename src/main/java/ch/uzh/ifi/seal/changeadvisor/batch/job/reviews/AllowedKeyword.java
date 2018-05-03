package ch.uzh.ifi.seal.changeadvisor.batch.job.reviews;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

class AllowedKeyword {

    static final String STORE = "store";

    static final String INPUT_FILE = "input_file";

    static final String OUTPUT_FILE = "output_file";

    static final String FROM = "from";

    static final String TO = "to";

    static final String LIMIT = "limit";

    static final String THREAD = "thread";

    static final String PHANTOM_JS = "phantomJS_path";

    static final String REVIEWS_FOR = "get_reviews_for";

    static final String EXPORT_TO = "export_to";

    private static final Set<String> ALLOWED_KEYWORDS;

    static {
        ALLOWED_KEYWORDS = new ImmutableSet.Builder<String>()
                .add("store")
                .add("input_file")
                .add("output_file")
                .add("from")
                .add("to")
                .add("limit")
                .add("thread")
                .add("phantomJS_path")
                .add("get_reviews_for")
                .add("export_to")
                .build();
    }

    static boolean isAllowed(String key) {
        return ALLOWED_KEYWORDS.contains(key);
    }
}
