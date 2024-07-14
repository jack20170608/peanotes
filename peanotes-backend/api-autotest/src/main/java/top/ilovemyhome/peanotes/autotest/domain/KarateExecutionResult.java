package top.ilovemyhome.peanotes.autotest.domain;

import top.ilovemyhome.peanotes.backend.common.db.dao.common.Persistable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KarateExecutionResult implements Persistable<Long> {

    private Long id;
    private final Long karateExecutionId;
    private final int threadCount;
    private final int featureCount;
    private final int passCount;
    private final int failCount;
    private final int skipCount;

    private final LocalDateTime startDt;
    private final LocalDateTime endDt;
    private final Duration takeTime;
    private final Map<String, String> failedMap;
    private final String failedReason;

    private KarateExecution karateExecution;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return Objects.isNull(id) || id == 0;
    }



    private KarateExecutionResult(Long id, Long karateExecutionId, int threadCount, int featureCount, int passCount, int failCount, int skipCount, LocalDateTime startDt, LocalDateTime endDt, Duration takeTime, Map<String, String> failedMap, String failureReason) {
        this.id = id;
        this.karateExecutionId = karateExecutionId;
        this.threadCount = threadCount;
        this.featureCount = featureCount;
        this.passCount = passCount;
        this.failCount = failCount;
        this.skipCount = skipCount;
        this.startDt = startDt;
        this.endDt = endDt;
        this.takeTime = takeTime;
        this.failedMap = failedMap;
        this.failedReason = failureReason;
    }

    public enum Field {
        id("ID", true),
        karateExecutionId("KARATE_EXECUTION_ID"),
        threadCount("THREAD_COUNT"),
        featureCount("FEATURE_COUNT"),
        passCount("PASS_COUNT"),
        failCount("FAIL_COUNT"),
        skipCount("SKIP_COUNT"),
        startDt("START_DT"),
        endDt("END_DT"),
        takeTime("TAKE_TIME"),
        failedMap("FAILED_MAP"),
        failedReason("FAILED_REASON");

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

    public static final Map<String, String> FIELD_COLUMN_MAP
        = Collections.unmodifiableMap(Stream.of(KarateRequest.Field.values())
        .collect(Collectors.toMap(KarateRequest.Field::name, KarateRequest.Field::getDbColumn)));

    public static final String ID_FIELD = KarateRequest.Field.id.name();


    public void setId(Long id) {
        this.id = id;
    }

    public Long getKarateExecutionId() {
        return karateExecutionId;
    }

    public void setKarateExecution(KarateExecution karateExecution) {
        this.karateExecution = karateExecution;
    }

    public KarateExecution getKarateExecution() {
        return karateExecution;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public int getFeatureCount() {
        return featureCount;
    }

    public int getPassCount() {
        return passCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public int getSkipCount() {
        return skipCount;
    }

    public LocalDateTime getStartDt() {
        return startDt;
    }

    public LocalDateTime getEndDt() {
        return endDt;
    }

    public Duration getTakeTime() {
        return takeTime;
    }

    public Map<String, String> getFailedMap() {
        return failedMap;
    }

    public String getFailedReason() {
        return failedReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KarateExecutionResult that = (KarateExecutionResult) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "KarateExecutionResult{" +
            "id=" + id +
            ", karateExecutionId=" + karateExecutionId +
            ", threadCount=" + threadCount +
            ", featureCount=" + featureCount +
            ", passCount=" + passCount +
            ", failCount=" + failCount +
            ", skipCount=" + skipCount +
            ", startDt=" + startDt +
            ", endDt=" + endDt +
            ", takeTime=" + takeTime +
            ", failedMap=" + failedMap +
            ", failedReason='" + failedReason + '\'' +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private Long karateExecutionId;
        private int threadCount;
        private int featureCount;
        private int passCount;
        private int failCount;
        private int skipCount;
        private LocalDateTime startDt;
        private LocalDateTime endDt;
        private Duration takeTime;
        private Map<String, String> failedMap;
        private String failedReason;

        private Builder() {
        }


        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withKarateExecutionId(Long karateExecutionId) {
            this.karateExecutionId = karateExecutionId;
            return this;
        }

        public Builder withThreadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }

        public Builder withFeatureCount(int featureCount) {
            this.featureCount = featureCount;
            return this;
        }

        public Builder withPassCount(int passCount) {
            this.passCount = passCount;
            return this;
        }

        public Builder withFailCount(int failCount) {
            this.failCount = failCount;
            return this;
        }

        public Builder withSkipCount(int skipCount) {
            this.skipCount = skipCount;
            return this;
        }

        public Builder withStartDt(LocalDateTime startDt) {
            this.startDt = startDt;
            return this;
        }

        public Builder withEndDt(LocalDateTime endDt) {
            this.endDt = endDt;
            return this;
        }

        public Builder withTakeTime(Duration takeTime) {
            this.takeTime = takeTime;
            return this;
        }

        public Builder withFailedMap(Map<String, String> failedMap) {
            this.failedMap = failedMap;
            return this;
        }

        public Builder withFailed(String failedReason) {
            this.failedReason = failedReason;
            return this;
        }

        public KarateExecutionResult build() {
            return new KarateExecutionResult(id, karateExecutionId, threadCount, featureCount, passCount, failCount, skipCount, startDt, endDt, takeTime, failedMap, failedReason);
        }
    }
}
