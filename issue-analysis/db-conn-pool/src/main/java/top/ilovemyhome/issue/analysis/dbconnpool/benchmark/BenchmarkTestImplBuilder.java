package top.ilovemyhome.issue.analysis.dbconnpool.benchmark;

import top.ilovemyhome.issue.analysis.dbconnpool.common.testsuite.TestSuite;
import top.ilovemyhome.issue.analysis.dbconnpool.common.testsuite.TransactionOperation;

import java.sql.Connection;
import java.util.function.Supplier;

public final class BenchmarkTestImplBuilder {

    private boolean autoCommit;
    private int threadCount;
    private TestSuite testSuite;
    private int transactionsPerThread;
    private Supplier<Connection> connectionSupplier;
    private Supplier<TransactionOperation> userDefinedOperationSupplier;

    public BenchmarkTestImplBuilder() {
    }

    public BenchmarkTestImplBuilder withAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
        return this;
    }

    public BenchmarkTestImplBuilder withThreadCount(int threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    public BenchmarkTestImplBuilder withTestSuite(TestSuite testSuite) {
        this.testSuite = testSuite;
        return this;
    }

    public BenchmarkTestImplBuilder withTransactionsPerThread(int transactionsPerThread) {
        this.transactionsPerThread = transactionsPerThread;
        return this;
    }

    public BenchmarkTestImplBuilder withConnectionSupplier(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
        return this;
    }

    public BenchmarkTestImplBuilder withUserDefinedOperationSupplier(Supplier<TransactionOperation> userDefinedOperationSupplier) {
        this.userDefinedOperationSupplier = userDefinedOperationSupplier;
        return this;
    }

    public BenchmarkTestImpl build() {
        return new BenchmarkTestImpl(autoCommit, threadCount, testSuite, transactionsPerThread, connectionSupplier, userDefinedOperationSupplier);
    }
}
