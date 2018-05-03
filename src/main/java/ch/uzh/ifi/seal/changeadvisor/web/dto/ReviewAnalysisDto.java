package ch.uzh.ifi.seal.changeadvisor.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ReviewAnalysisDto {

    @NotNull
    @Size(min = 1)
    private String app;

    public ReviewAnalysisDto(@JsonProperty("app") String app) {
        this.app = app;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    @Override
    public String toString() {
        return "ReviewAnalysisDto{" +
                "app='" + app + '\'' +
                '}';
    }
}
