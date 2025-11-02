package top.ilovemyhome.issue.analysis.dbconnpool.common;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.issue.analysis.dbconnpool.common.enums.OperationStrategy;
import top.ilovemyhome.issue.analysis.dbconnpool.common.operation.DbOperation;
import top.ilovemyhome.issue.analysis.dbconnpool.common.operation.DbOperationHelper;
import top.ilovemyhome.issue.analysis.dbconnpool.common.operation.OperationType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class BenchmarkTask implements Runnable {

    private final boolean autoCommit;
    private final BenchmarkTest benchmarkTest;
    private final int threadId;
    private final int operationsPerTransaction;
    private final int transactionsPerThread;
    private final OperationStrategy operationStrategy;
    private final Supplier<List<DbOperation>> userDefinedOperationSupplier;

    public BenchmarkTask(BenchmarkTest benchmarkTest
        , boolean autoCommit
        , int threadId
        , int operationsPerTransaction
        , int transactionsPerThread
        , OperationStrategy operationStrategy
        , Supplier<List<DbOperation>> userDefinedOperations) {
        this.autoCommit = autoCommit;
        this.benchmarkTest = benchmarkTest;
        this.threadId = threadId;
        this.operationsPerTransaction = operationsPerTransaction;
        this.transactionsPerThread = transactionsPerThread;
        this.operationStrategy = operationStrategy;
        this.userDefinedOperationSupplier = userDefinedOperations;
    }


    @Override
    public void run() {
        Supplier<Connection> connectionSupplier = benchmarkTest.getConnectionSupplier();
        //Loop transaction per thread
        for (int i = 0; i < transactionsPerThread; i++) {
            long transactionStart = System.currentTimeMillis();
            boolean success = true;
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
                    switch (operationStrategy) {
                        case ALL_RANDOM ->
                            allRandomOperations(connection, operationsPerTransaction);
                        case ALL_INSERT ->
                            allInsertOperations(connection, operationsPerTransaction);
                        case ALL_UPDATE ->
                            allUpdateOperations(connection, operationsPerTransaction);
                        case ALL_DELETE ->
                            allDeleteOperations(connection, operationsPerTransaction);
                        case USER_DEFINED -> userDefinedOperations(connection);
                        default ->
                            throw new IllegalArgumentException("Invalid operation strategy: " + operationStrategy);
                    }
                    if (!autoCommit) {
                        connection.commit();
                    }
                } catch (Throwable t) {
                    logger.error("Thread {} error: {}", threadId, t.getMessage(), t);
                    success = false;
                    try {
                        connection.rollback();
                        benchmarkTest.rollback();
                        logger.info("Thread {} rolled back transaction: {}", threadId, t.getMessage());
                    } catch (SQLException rollbackEx) {
                        logger.error("Thread {} rollback failed: {}", threadId, rollbackEx.getMessage());
                    }
                    logger.error("Thread {} error: {}", threadId, t.getMessage(), t);
                } finally {
                    if (success) {
                        long transactionTime = System.currentTimeMillis() - transactionStart;
                        benchmarkTest.commit(transactionTime);
                        logger.info("Thread {} transaction time: {} ms", threadId, transactionTime);
                    }
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

    private void userDefinedOperations(Connection connection) {
        userDefinedOperationSupplier.get()
            .forEach(operation -> operation.execute(connection, false));
    }

    private void allDeleteOperations(Connection connection, int operationsPerTransaction) {
        for (int j = 0; j < operationsPerTransaction; j++) {
            DbOperationHelper.getDbOperation(benchmarkTest, OperationType.DELETE)
                .execute(connection, false);
        }
    }

    private void allInsertOperations(Connection connection, int operationsPerTransaction) {
        for (int j = 0; j < operationsPerTransaction; j++) {
            DbOperationHelper.getDbOperation(benchmarkTest, OperationType.INSERT)
                .execute(connection, false);
        }
    }

    private void allUpdateOperations(Connection connection, int operationsPerTransaction) {
        for (int j = 0; j < operationsPerTransaction; j++) {
            DbOperationHelper.getDbOperation(benchmarkTest, OperationType.UPDATE)
                .execute(connection, false);
        }
    }

    private void allRandomOperations(Connection connection, int operationsPerTransaction) {
        for (int j = 0; j < operationsPerTransaction; j++) {
            executeRandomOperation(connection);
        }
    }

    private void executeRandomOperation(Connection connection) {
        OperationType opType = OperationType.values()[RandomUtils.secure()
            .randomInt(0, OperationType.values().length)];
        OperationType actualOpType = opType;
        if ((opType == OperationType.DELETE
            || opType == OperationType.QUERY
            || opType == OperationType.UPDATE)
            && SharedResources.insertedIds.isEmpty()) {
            actualOpType = OperationType.INSERT;
        }
        DbOperationHelper.getDbOperation(benchmarkTest, actualOpType)
            .execute(connection, false);
    }

    private void recordOperation(OperationType opType, long executionTime) {
        switch (opType) {
            case INSERT -> benchmarkTest.successInsert(executionTime, 1);
            case QUERY -> benchmarkTest.successQuery(executionTime, 1);
            case UPDATE -> benchmarkTest.successUpdate(executionTime, 1);
            case DELETE -> benchmarkTest.successDelete(executionTime, 1);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkTask.class);
}
