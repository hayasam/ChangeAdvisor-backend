package ch.uzh.ifi.seal.changeadvisor.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("unused")
public class SourceConfig implements Serializable {

    private final Date lastSourceImport;

    @JsonCreator
    public SourceConfig(@JsonProperty("lastSourceImport") Date lastSourceImport) {
        this.lastSourceImport = lastSourceImport;
    }

    public Date getLastSourceImport() {
        return lastSourceImport;
    }

    @Override
    public String toString() {
        return "SourceConfig{" +
                "lastSourceImport=" + lastSourceImport +
                '}';
    }
}
