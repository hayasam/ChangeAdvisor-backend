package ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf;

import ch.uzh.ifi.seal.changeadvisor.tfidf.AbstractNGram;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Label implements Comparable<Label> {

    @Id
    private String id;

    private String appName;

    private String category;

    private int ngramSize;

    private String token;

    private double score;

    public Label() {
    }

    public Label(String appName, String category, String token, Double score) {
        this.appName = appName;
        this.token = token;
        this.score = score;
        this.ngramSize = token.split(" ").length;
        this.category = category;
    }

    public Label(String appName, String category, AbstractNGram token, Double score) {
        this.appName = appName;
        this.token = token.toString();
        this.score = score;
        this.ngramSize = token.ngramSize();
        this.category = category;

    }

    public String getId() {
        return id;
    }

    public String getAppName() {
        return appName;
    }

    public String getLabel() {
        return getToken();
    }

    public double getScore() {
        return score;
    }

    public String getCategory() {
        return category;
    }

    public int getNgramSize() {
        return ngramSize;
    }

    public String getToken() {
        return token;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setNgramSize(int ngramSize) {
        this.ngramSize = ngramSize;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Label label = (Label) o;

        if (Double.compare(label.score, score) != 0) return false;
        if (appName != null ? !appName.equals(label.appName) : label.appName != null) return false;
        return token != null ? token.equals(label.token) : label.token == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = appName != null ? appName.hashCode() : 0;
        result = 31 * result + (token != null ? token.hashCode() : 0);
        temp = Double.doubleToLongBits(score);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public int compareTo(@NotNull Label o) {
        return Double.compare(score, o.score);
    }

    @Override
    public String toString() {
        return "Label{" +
                "token='" + token + '\'' +
                ", score=" + score +
                '}';
    }
}
