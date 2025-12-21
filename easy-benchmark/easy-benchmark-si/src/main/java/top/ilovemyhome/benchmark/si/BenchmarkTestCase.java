package top.ilovemyhome.benchmark.si;


import top.ilovemyhome.benchmark.si.enums.BenchmarkType;
import top.ilovemyhome.benchmark.si.enums.JdbcClientType;
import top.ilovemyhome.benchmark.si.enums.JdbcConnectionPoolType;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * Represents a benchmark test case configuration that defines the parameters for executing a database benchmark.
 * <p>
 * This record encapsulates all the necessary configuration details for running a benchmark, including
 * benchmark type, JDBC client type, connection pool type, data source configuration, and execution parameters.
 * <p>
 * The record includes validation logic in its compact constructor to ensure that all required fields are
 * properly set and numeric values are positive.
 * <p>
 * A Builder pattern is also provided for convenient creation of instances.
 *
 * @param id                   Unique identifier for the benchmark test case
 * @param name                 Name of the benchmark test case
 * @param type                 Type of benchmark to execute (e.g., TPC-C, TPC-H, etc.)
 * @param jdbcClientType       Type of JDBC client to use (JDBC or JDBI)
 * @param connectionPoolType   Type of connection pool to use
 * @param dataSourceConfig     Configuration for the data source
 * @param testRound            Number of test rounds to execute
 * @param threadCount          Number of threads to use for the benchmark
 * @param transactionPerThread Number of transactions each thread should execute
 * @param createDt             Creation date and time of the test case
 * @param lastUpdateDt         Last update date and time of the test case
 */
public record BenchmarkTestCase(
    Long id
    , String name
    , BenchmarkType type
    , JdbcClientType jdbcClientType
    , JdbcConnectionPoolType connectionPoolType
    , DataSourceConfig dataSourceConfig

    , int testRound
    , int threadCount
    , int transactionPerThread
    , LocalDateTime createDt
    , LocalDateTime lastUpdateDt) {

    /**
     * Compact constructor that validates the input parameters.
     * <p>
     * Validates that:
     * <ul>
     *   <li>type is not null</li>
     *   <li>jdbcClientType is not null</li>
     *   <li>connectionPoolType is not null</li>
     *   <li>dataSourceConfig is not null</li>
     *   <li>testRound is greater than 0</li>
     *   <li>threadCount is greater than 0</li>
     *   <li>transactionPerThread is greater than 0</li>
     * </ul>
     *
     * @throws IllegalArgumentException if any of the validation rules are violated
     */
    public BenchmarkTestCase {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (jdbcClientType == null) {
            throw new IllegalArgumentException("jdbcClientType must not be null");
        }
        if (connectionPoolType == null) {
            throw new IllegalArgumentException("connectionPoolType must not be null");
        }
        if (dataSourceConfig == null) {
            throw new IllegalArgumentException("dataSourceConfig must not be null");
        }
        if (testRound <= 0) {
            throw new IllegalArgumentException("testRound must be greater than 0");
        }
        if (threadCount <= 0) {
            throw new IllegalArgumentException("threadCount must be greater than 0");
        }
        if (transactionPerThread <= 0) {
            throw new IllegalArgumentException("transactionPerThread must be greater than 0");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(BenchmarkTestCase testCase) {
        Objects.requireNonNull(testCase, "testCase must not be null");
        return new Builder()
            .withId(testCase.id())
            .withName(testCase.name())
            .withType(testCase.type())
            .withJdbcClientType(testCase.jdbcClientType())
            .withConnectionPoolType(testCase.connectionPoolType())
            .withDataSourceConfig(testCase.dataSourceConfig())
            .withTestRound(testCase.testRound())
            .withThreadCount(testCase.threadCount())
            .withTransactionPerThread(testCase.transactionPerThread())
            .withCreateDt(testCase.createDt())
            .withLastUpdateDt(testCase.lastUpdateDt());
    }

    public static final class Builder {
        private Long id;
        private String name;
        private BenchmarkType type;
        private JdbcClientType jdbcClientType;
        private JdbcConnectionPoolType connectionPoolType;
        private DataSourceConfig dataSourceConfig;
        private int testRound;
        private int threadCount;
        private int transactionPerThread;
        private LocalDateTime createDt;
        private LocalDateTime lastUpdateDt;

        private Builder() {
        }


        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withType(BenchmarkType type) {
            this.type = type;
            return this;
        }

        public Builder withJdbcClientType(JdbcClientType jdbcClientType) {
            this.jdbcClientType = jdbcClientType;
            return this;
        }

        public Builder withConnectionPoolType(JdbcConnectionPoolType connectionPoolType) {
            this.connectionPoolType = connectionPoolType;
            return this;
        }

        public Builder withDataSourceConfig(DataSourceConfig dataSourceConfig) {
            this.dataSourceConfig = dataSourceConfig;
            return this;
        }

        public Builder withTestRound(int testRound) {
            this.testRound = testRound;
            return this;
        }

        public Builder withThreadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }

        public Builder withTransactionPerThread(int transactionPerThread) {
            this.transactionPerThread = transactionPerThread;
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

        public BenchmarkTestCase build() {
            return new BenchmarkTestCase(id, name, type, jdbcClientType, connectionPoolType, dataSourceConfig, testRound, threadCount, transactionPerThread, createDt, lastUpdateDt);
        }
    }
}
