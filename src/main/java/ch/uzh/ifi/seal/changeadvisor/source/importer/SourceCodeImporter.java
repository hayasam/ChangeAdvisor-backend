package ch.uzh.ifi.seal.changeadvisor.source.importer;

import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;

public interface SourceCodeImporter {

    SourceCodeDirectory importSource();
}
