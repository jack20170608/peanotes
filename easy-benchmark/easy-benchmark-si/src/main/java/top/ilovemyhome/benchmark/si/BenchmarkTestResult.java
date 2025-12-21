package top.ilovemyhome.benchmark.si;

import top.ilovemyhome.benchmark.si.enums.State;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BenchmarkTestResult(
    Long id
    , Long testCaseId
    , LocalDateTime startDt
    , LocalDateTime endDt
    , Long totalTimeMilliseconds
    , State state
    , boolean success
    , String errorMessage
    , String errorStackTrace
    //The thread info
    , int testRound
    , int totalThreadCount
    , int failedThreadCount
    , int successThreadCount

    //The transaction result
    , int totalTransactionCount
    , int commitTransactionCount
    , int rollbackTransactionCount

    //The statistic result
    , BigDecimal avgCommitTime
    , BigDecimal avgRollbackTime
    , BigDecimal tps
    , BigDecimal successRate

    , LocalDateTime createDt
    , LocalDateTime lastUpdateDt) {

    public static Builder builder(BenchmarkTestResult result) {
        return new Builder()
            .withId(result.id)
            .withTestCaseId(result.testCaseId)
            .withStartDt(result.startDt)
            .withEndDt(result.endDt)
            .withTotalTimeMilliseconds(result.totalTimeMilliseconds)
            .withState(result.state)
            .withSuccess(result.success)
            .withErrorMessage(result.errorMessage)
            .withErrorStackTrace(result.errorStackTrace)
            .withTestRound(result.testRound)
            .withTotalThreadCount(result.totalThreadCount)
            .withFailedThreadCount(result.failedThreadCount)
            .withSuccessThreadCount(result.successThreadCount)
            .withTotalTransactionCount(result.totalTransactionCount)
            .withCommitTransactionCount(result.commitTransactionCount)
            .withRollbackTransactionCount(result.rollbackTransactionCount)
            .withAvgCommitTimeMilliseconds(result.avgCommitTime)
            .withAvgRollbackTimeMilliseconds(result.avgRollbackTime)
            .withTps(result.tps)
            .withSuccessRate(result.successRate)
            .withCreateDt(result.createDt)
            .withLastUpdateDt(result.lastUpdateDt);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private Long testCaseId;
        private LocalDateTime startDt;
        private LocalDateTime endDt;
        private Long totalTimeMilliseconds;
        private State state;
        private boolean success;
        private String errorMessage;
        private String errorStackTrace;
        private int testRound;
        private int totalThreadCount;
        private int failedThreadCount;
        private int successThreadCount;
        private int totalTransactionCount;
        private int commitTransactionCount;
        private int rollbackTransactionCount;
        private BigDecimal avgCommitTimeMilliseconds;
        private BigDecimal avgRollbackTimeMilliseconds;
        private BigDecimal tps;
        private BigDecimal successRate;
        private LocalDateTime createDt;
        private LocalDateTime lastUpdateDt;

        private Builder() {
        }


        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withTestCaseId(Long testCaseId) {
            this.testCaseId = testCaseId;
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

        public Builder withTotalTimeMilliseconds(Long totalTimeMilliseconds) {
            this.totalTimeMilliseconds = totalTimeMilliseconds;
            return this;
        }

        public Builder withState(State state) {
            this.state = state;
            return this;
        }

        public Builder withSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public Builder withErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder withErrorStackTrace(String errorStackTrace) {
            this.errorStackTrace = errorStackTrace;
            return this;
        }

        public Builder withTestRound(int testRound) {
            this.testRound = testRound;
            return this;
        }

        public Builder withTotalThreadCount(int totalThreadCount) {
            this.totalThreadCount = totalThreadCount;
            return this;
        }

        public Builder withFailedThreadCount(int failedThreadCount) {
            this.failedThreadCount = failedThreadCount;
            return this;
        }

        public Builder withSuccessThreadCount(int successThreadCount) {
            this.successThreadCount = successThreadCount;
            return this;
        }

        public Builder withTotalTransactionCount(int totalTransactionCount) {
            this.totalTransactionCount = totalTransactionCount;
            return this;
        }

        public Builder withCommitTransactionCount(int commitTransactionCount) {
            this.commitTransactionCount = commitTransactionCount;
            return this;
        }

        public Builder withRollbackTransactionCount(int rollbackTransactionCount) {
            this.rollbackTransactionCount = rollbackTransactionCount;
            return this;
        }

        public Builder withAvgCommitTimeMilliseconds(BigDecimal avgCommitTimeMilliseconds) {
            this.avgCommitTimeMilliseconds = avgCommitTimeMilliseconds;
            return this;
        }

        public Builder withAvgRollbackTimeMilliseconds(BigDecimal avgRollbackTimeMilliseconds) {
            this.avgRollbackTimeMilliseconds = avgRollbackTimeMilliseconds;
            return this;
        }

        public Builder withTps(BigDecimal tps) {
            this.tps = tps;
            return this;
        }

        public Builder withSuccessRate(BigDecimal successRate) {
            this.successRate = successRate;
            return this;
        }

        public Builder withCreateDt(LocalDateTime createDt) {
            this.createDt = createDt;
            return this;
        }

        public Builder withLastUpdateDt(LocalDateTime lastUpdateDt) {
            this.lastUpdateDt = lastUpdateDt;
            return this;
        }

        public BenchmarkTestResult build() {
            return new BenchmarkTestResult(id, testCaseId, startDt, endDt, totalTimeMilliseconds, state, success, errorMessage, errorStackTrace, testRound, totalThreadCount
                , failedThreadCount, successThreadCount, totalTransactionCount, commitTransactionCount
                , rollbackTransactionCount, avgCommitTimeMilliseconds, avgRollbackTimeMilliseconds, tps, successRate, createDt, lastUpdateDt);
        }
    }
}
