package ch.uzh.ifi.seal.changeadvisor.schedule;

import org.springframework.scheduling.config.CronTask;

public class ReviewImportCronTask extends CronTask {

    private final String googlePlayId;

    public ReviewImportCronTask(Runnable runnable, String expression, String googlePlayId) {
        super(runnable, expression);
        this.googlePlayId = googlePlayId;
    }

    public String getGooglePlayId() {
        return googlePlayId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReviewImportCronTask that = (ReviewImportCronTask) o;

        return googlePlayId != null ? googlePlayId.equals(that.googlePlayId) : that.googlePlayId == null;
    }

    @Override
    public int hashCode() {
        return googlePlayId != null ? googlePlayId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ReviewImportCronTask{" +
                "cron='" + getExpression() + '\'' +
                "googlePlayId='" + googlePlayId + '\'' +
                '}';
    }
}
