package top.ilovemyhome.issue.analysis.dbconnpool.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.issue.analysis.dbconnpool.common.enums.OperationStrategy;
import top.ilovemyhome.issue.analysis.dbconnpool.common.operation.CreateTableOperation;
import top.ilovemyhome.issue.analysis.dbconnpool.common.operation.DbOperation;
import top.ilovemyhome.issue.analysis.dbconnpool.common.operation.DbOperationHelper;
import top.ilovemyhome.issue.analysis.dbconnpool.common.operation.InsertOperation;
import top.ilovemyhome.issue.analysis.dbconnpool.util.SplitLineHelper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class BenchmarkTest implements Runnable {

    private final AtomicLong totalTime = new AtomicLong(0);
    private final AtomicLong errorThreadCount = new AtomicLong(0);
    private final AtomicLong successThreadCount = new AtomicLong(0);

    private final AtomicLong totalInsertTime = new AtomicLong(0);
    private final AtomicLong totalQueryTime = new AtomicLong(0);
    private final AtomicLong totalUpdateTime = new AtomicLong(0);
    private final AtomicLong totalDeleteTime = new AtomicLong(0);

    private final AtomicLong insertCount = new AtomicLong(0);
    private final AtomicLong queryCount = new AtomicLong(0);
    private final AtomicLong updateCount = new AtomicLong(0);
    private final AtomicLong deleteCount = new AtomicLong(0);

    private final AtomicLong rollbackCount = new AtomicLong(0);
    private final AtomicLong commitCount = new AtomicLong(0);
    private final AtomicLong totalTransactionTime = new AtomicLong(0);

    private final boolean printDetails = true;
    //This is control if the transaction support
    private final boolean autoCommit;
    private final int threadCount;
    private final int operationsPerTransaction;
    private final OperationStrategy operationStrategy;
    private final int transactionsPerThread;
    private final Supplier<Connection> connectionSupplier;
    private final Supplier<List<DbOperation>> userDefinedOperationSupplier;

    private static final OperationStrategy DEFAULT_OPERATION_STRATEGY = OperationStrategy.ALL_RANDOM;
    private static final int DEFAULT_THREAD_COUNT = 1;
    private static final Supplier<Connection> DEFAULT_CONNECTION_SUPPLIER = () -> null;
    private static final int DEFAULT_OPERATIONS_PER_TRANSACTION = 5;
    private static final int DEFAULT_TRANSACTIONS_PER_THREAD = 100;

    private BenchmarkTest(boolean autoCommit, int threadCount, int operationsPerTransaction
        , OperationStrategy operationStrategy, int transactionsPerThread
        , Supplier<Connection> connectionSupplier, Supplier<List<DbOperation>> userDefinedOperationSupplier) {
        this.autoCommit = autoCommit;
        this.threadCount = threadCount;
        this.operationStrategy = operationStrategy;
        this.operationsPerTransaction = operationsPerTransaction;
        this.transactionsPerThread = transactionsPerThread;
        this.connectionSupplier = connectionSupplier;
        this.userDefinedOperationSupplier = userDefinedOperationSupplier;

        if (this.operationStrategy == OperationStrategy.USER_DEFINED) {
            if (Objects.isNull(this.userDefinedOperationSupplier)
                || this.userDefinedOperationSupplier.get().size() != this.operationsPerTransaction) {
                throw new IllegalArgumentException("The number of user-defined operations must be equal to operationsPerTransaction");
            }
        }
    }

    private BenchmarkTest() {
        this(true, DEFAULT_THREAD_COUNT, DEFAULT_OPERATIONS_PER_TRANSACTION, DEFAULT_OPERATION_STRATEGY
            , DEFAULT_TRANSACTIONS_PER_THREAD, DEFAULT_CONNECTION_SUPPLIER, null);
    }

    @Override
    public void run() {
        // Initialize test table
        var createTableOperation = new CreateTableOperation(this);
        createTableOperation.execute(connectionSupplier.get(), true);
        //Warm up insert given data in batch mode
        var insertOperation = new InsertOperation( this);

        // Create thread pool
        long startTime = System.currentTimeMillis();
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            List<Future<?>> futures = new ArrayList<>();
            // Submit test tasks
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(new BenchmarkTask(this, autoCommit
                    , i, operationsPerTransaction, transactionsPerThread, operationStrategy
                    , userDefinedOperationSupplier)));
            }
            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                    successThreadCount.incrementAndGet();
                } catch (ExecutionException | InterruptedException e) {
                    logger.error("Test task execution error: {}", e.getCause().getMessage());
                    errorThreadCount.incrementAndGet();
                }
            }
            // Shutdown thread pool
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.totalTime.addAndGet(System.currentTimeMillis() - startTime);
        }
    }


    public Supplier<Connection> getConnectionSupplier() {
        return connectionSupplier;
    }

    public void successInsert(long insertTime, int insertCount) {
        this.totalInsertTime.addAndGet(insertTime);
        this.insertCount.addAndGet(insertCount);
    }

    public void successQuery(long queryTime, int queryCount) {
        this.totalQueryTime.addAndGet(queryTime);
        this.queryCount.addAndGet(queryCount);
    }

    public void successUpdate(long updateTime, int updateCount) {
        this.totalUpdateTime.addAndGet(updateTime);
        this.updateCount.addAndGet(updateCount);
    }

    public void successDelete(long deleteTime, int deleteCount) {
        this.totalDeleteTime.addAndGet(deleteTime);
        this.deleteCount.addAndGet(deleteCount);
    }

    public void error() {
        this.errorThreadCount.incrementAndGet();
    }

    public void rollback() {
        this.rollbackCount.incrementAndGet();
    }

    public void commit(long transactionTime) {
        this.commitCount.incrementAndGet();
        this.totalTransactionTime.addAndGet(transactionTime);
    }

    public void printResults() {
        // 计算总体操作数和吞吐量
        long totalOperations = insertCount.get() + queryCount.get() + updateCount.get() + deleteCount.get();
        long totalOperationsTime = totalInsertTime.get() + totalQueryTime.get() + totalUpdateTime.get() + totalDeleteTime.get();
        double overallThroughput = totalOperations > 0 ? (totalOperations * 1000.0) / totalOperationsTime : 0;
        double avgTransactionTime = commitCount.get() > 0 ? (double) totalTransactionTime.get() / commitCount.get() : 0;
        double transactionThroughput = commitCount.get() > 0 ? (commitCount.get() * 1000.0) / totalTime.get() : 0;

        // 打印标题
        logger.info(SplitLineHelper.getTitleSeparator("BENCHMARK RESULTS"));
        logger.info("======================================================");

        // 打印配置信息
        logger.info(String.format("%-35s: %s", "Connection Mode", autoCommit ? "Auto-Commit" : "Transaction"));
        logger.info(String.format("%-35s: %d", "Thread Count", threadCount));
        if (!autoCommit) {
            logger.info(String.format("%-35s: %d", "Operations per Transaction", operationsPerTransaction));
            logger.info(String.format("%-35s: %d", "Transactions per Thread", transactionsPerThread));
        }

        // 打印总体统计
        logger.info("------------------------------------------------------");
        logger.info(String.format("%-35s: %d ms", "Total Execution Time", totalTime.get()));
        logger.info(String.format("%-35s: %d", "Successful Threads", successThreadCount.get()));
        logger.info(String.format("%-35s: %d", "Failed Threads", errorThreadCount.get()));
        logger.info(String.format("%-35s: %d", "Total Operations", totalOperations));
        logger.info(String.format("%-35s: %.2f ops/sec", "Overall Throughput", overallThroughput));

        // 打印事务统计（如果启用）
        if (!autoCommit) {
            logger.info(String.format("%-35s: %d", "Total Commits", commitCount.get()));
            logger.info(String.format("%-35s: %d", "Total Rollbacks", rollbackCount.get()));
            logger.info(String.format("%-35s: %.2f ms", "Avg. Transaction Time", avgTransactionTime));
            logger.info(String.format("%-35s: %.2f txn/sec", "Transaction Throughput", transactionThroughput));
        }

        // 打印分隔线
        logger.info("------------------------------------------------------");
        logger.info(String.format("| %-12s | %-12s | %-12s | %-12s | %-12s |",
            "Operation", "Count", "Total Time", "Avg. Time", "Throughput"));
        logger.info("------------------------------------------------------");

        // 打印各类操作统计
        printOperationStats("Insert", insertCount.get(), totalInsertTime.get());
        printOperationStats("Query", queryCount.get(), totalQueryTime.get());
        printOperationStats("Update", updateCount.get(), totalUpdateTime.get());
        printOperationStats("Delete", deleteCount.get(), totalDeleteTime.get());

        // 打印结束线
        logger.info("======================================================");
    }

    private void printOperationStats(String operation, long count, long totalTime) {
        if (count == 0) {
            logger.info(String.format("| %-12s | %-12s | %-12s | %-12s | %-12s |",
                operation, "0", "0 ms", "0 ms", "0 ops/sec"));
            return;
        }

        double avgTime = (double) totalTime / count;
        double throughput = (count * 1000.0) / totalTime;

        logger.info(String.format("| %-12s | %-12d | %-12d | %-12.2f | %-12.2f |",
            operation, count, totalTime, avgTime, throughput));
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int transactionsPerThread;
        private int operationsPerTransaction;
        private int threadCount;
        private boolean autoCommit;
        private OperationStrategy operationStrategy;
        private Supplier<Connection> connectionSupplier;
        private Supplier<List<DbOperation>> userDefinedOperationSupplier;

        private Builder() {
        }

        public Builder withTransactionsPerThread(int transactionsPerThread) {
            this.transactionsPerThread = transactionsPerThread;
            return this;
        }

        public Builder withOperationsPerTransaction(int operationsPerTransaction) {
            this.operationsPerTransaction = operationsPerTransaction;
            return this;
        }

        public Builder withThreadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }

        public Builder withAutoCommit(boolean autoCommit) {
            this.autoCommit = autoCommit;
            return this;
        }

        public Builder withConnectionSupplier(Supplier<Connection> connectionSupplier) {
            this.connectionSupplier = connectionSupplier;
            return this;
        }

        public Builder withOperationStrategy(OperationStrategy operationStrategy) {
            this.operationStrategy = operationStrategy;
            return this;
        }

        public Builder withUserDefinedOperationSupplier(Supplier<List<DbOperation>> userDefinedOperationSupplier) {
            this.userDefinedOperationSupplier = userDefinedOperationSupplier;
            return this;
        }

        public BenchmarkTest build() {
            return new BenchmarkTest(autoCommit, threadCount, operationsPerTransaction, operationStrategy, transactionsPerThread, connectionSupplier, userDefinedOperationSupplier);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkTest.class);
}
