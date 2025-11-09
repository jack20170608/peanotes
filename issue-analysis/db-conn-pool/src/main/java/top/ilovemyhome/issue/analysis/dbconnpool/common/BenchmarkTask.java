package top.ilovemyhome.issue.analysis.dbconnpool.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.issue.analysis.dbconnpool.common.testsuite.TestSuite;
import top.ilovemyhome.issue.analysis.dbconnpool.common.testsuite.TestSuiteHelper;
import top.ilovemyhome.issue.analysis.dbconnpool.common.testsuite.TransactionOperation;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;

public class BenchmarkTask implements Runnable {

    private final boolean autoCommit;
    private final BenchmarkTest benchmarkTest;
    private final int threadId;
    private final int transactionsPerThread;
    private final TestSuite testSuite;
    private final OperationStrategy operationStrategy;
    private final Supplier<TransactionOperation> userDefinedOperationSupplier;

    public int getTransactionsPerThread() {
        return transactionsPerThread;
    }

    public TestSuite getTestSuite() {
        return testSuite;
    }

    public BenchmarkTask(BenchmarkTest benchmarkTest
        , boolean autoCommit
        , int threadId
        , int transactionsPerThread
        , TestSuite testSuite
        , Supplier<TransactionOperation> userDefinedOperationSupplier
        , OperationStrategy operationStrategy) {
        this.autoCommit = autoCommit;
        this.benchmarkTest = benchmarkTest;
        this.threadId = threadId;
        this.transactionsPerThread = transactionsPerThread;
        this.testSuite = testSuite;
        this.userDefinedOperationSupplier = userDefinedOperationSupplier;
        this.operationStrategy = operationStrategy;
    }


    @Override
    public void run() {
        Supplier<Connection> connectionSupplier = benchmarkTest.getConnectionSupplier();
        //Loop transaction per thread
        for (int i = 0; i < transactionsPerThread; i++) {
            long transactionStart = System.currentTimeMillis();
            Connection connection = null;
            try {
                connection = connectionSupplier.get();
                Objects.requireNonNull(connection, "Connection cannot be null");
                //Set the autocommit flag to the desired value
                try {
                    if (connection.getAutoCommit() != autoCommit) {
                        logger.info("Thread {} autoCommit: {}", threadId, autoCommit);
                        connection.setAutoCommit(autoCommit);
                    }
                } catch (SQLException e) {
                    logger.error("Thread {} error: {}", threadId, e.getMessage(), e);
                    throw new RuntimeException("Error setting autoCommit: " + autoCommit, e);
                }
                //loop operation per transaction
                try {
                    TransactionOperation operation = TestSuiteHelper.getTransactionOperation(this);
                    operation.execute(connection, false);
                } catch (Throwable t) {
                    logger.error("Thread {} error: {}", threadId, t.getMessage(), t);
                }
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        logger.warn("Thread {} failed to close connection: {}", threadId, e.getMessage());
                    }
                }
            }
        }
    }

    private void userDefinedOperation(Connection connection) {
        TransactionOperation operation = userDefinedOperationSupplier.get();
        if (operation != null) {
            operation.execute(connection, false);
        }
    }


    public OperationStrategy getOperationStrategy() {
        return operationStrategy;
    }

    public Supplier<TransactionOperation> getUserDefinedOperationSupplier() {
        return userDefinedOperationSupplier;
    }

    public BenchmarkTest getBenchmarkTest() {
        return benchmarkTest;
    }

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkTask.class);
}
