package top.ilovemyhome.peanotes.common.task.admin.domain;

import com.google.common.collect.ImmutableMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JobLogReport {

    private Long id;
    private LocalDate triggerDate;
    private int runningCount;
    private int sucCount;
    private int failCount;
    private LocalDateTime lastUpdateDt;


    public enum Field {
        id("ID", true)
        , triggerDate("TRIGGER_DATE")
        , runningCount("RUNNING_COUNT")
        , sucCount("SUC_COUNT")
        , failCount("FAIL_COUNT")
        , lastUpdateDt("LAST_UPDATE_DT");

        private final String dbColumn;
        private final boolean isId;

        Field(String dbColumn) {
            this.dbColumn = dbColumn;
            this.isId = false;
        }

        Field(String dbColumn, boolean isId) {
            this.dbColumn = dbColumn;
            this.isId = isId;
        }

        public String getDbColumn() {
            return dbColumn;
        }

        public boolean isId() {
            return isId;
        }
    }

    public static final Map<String, String> FIELD_COLUMN_MAP = ImmutableMap.copyOf(Stream.of(JobLogReport.Field.values())
        .collect(Collectors.toMap(JobLogReport.Field::name, JobLogReport.Field::getDbColumn)));

    public Long getId() {
        return id;
    }

    public LocalDate getTriggerDate() {
        return triggerDate;
    }

    public int getRunningCount() {
        return runningCount;
    }

    public int getSucCount() {
        return sucCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public LocalDateTime getLastUpdateDt() {
        return lastUpdateDt;
    }

    public void setTriggerDate(LocalDate triggerDate) {
        this.triggerDate = triggerDate;
    }

    public void setRunningCount(int runningCount) {
        this.runningCount = runningCount;
    }

    public void setSucCount(int sucCount) {
        this.sucCount = sucCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public void setLastUpdateDt(LocalDateTime lastUpdateDt) {
        this.lastUpdateDt = lastUpdateDt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private LocalDateTime lastUpdateDt;
        private int failCount;
        private int sucCount;
        private int runningCount;
        private LocalDate triggerDate;
        private Long id;

        private Builder() {
        }



        public Builder withLastUpdateDt(LocalDateTime lastUpdateDt) {
            this.lastUpdateDt = lastUpdateDt;
            return this;
        }

        public Builder withFailCount(int failCount) {
            this.failCount = failCount;
            return this;
        }

        public Builder withSucCount(int sucCount) {
            this.sucCount = sucCount;
            return this;
        }

        public Builder withRunningCount(int runningCount) {
            this.runningCount = runningCount;
            return this;
        }

        public Builder withTriggerDate(LocalDate triggerDate) {
            this.triggerDate = triggerDate;
            return this;
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public JobLogReport build() {
            JobLogReport jobLogReport = new JobLogReport();
            jobLogReport.triggerDate = this.triggerDate;
            jobLogReport.sucCount = this.sucCount;
            jobLogReport.id = this.id;
            jobLogReport.runningCount = this.runningCount;
            jobLogReport.failCount = this.failCount;
            jobLogReport.lastUpdateDt = this.lastUpdateDt;
            return jobLogReport;
        }
    }
}
