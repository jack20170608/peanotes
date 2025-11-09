package top.ilovemyhome.issue.analysis.dbconnpool.benchmark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.commons.common.io.ResourceUtil;
import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkMonitor;
import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkTask;
import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkTest;
import top.ilovemyhome.issue.analysis.dbconnpool.common.OperationStrategy;
import top.ilovemyhome.issue.analysis.dbconnpool.common.testsuite.TestSuite;
import top.ilovemyhome.issue.analysis.dbconnpool.common.testsuite.TransactionOperation;
import top.ilovemyhome.issue.analysis.dbconnpool.util.SqlScriptsHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class BenchmarkTestImpl implements BenchmarkTest {

    public static BenchmarkTestImplBuilder builder() {
        return new BenchmarkTestImplBuilder();
    }

    @Override
    public void initSchema() {
        String resourceFolder = testSuite.getResourceFolder();
        String path = String.format("benchmarks/%s/init_schema_postgres.sql", resourceFolder);
        String createTableSql = ResourceUtil.getClasspathResourceAsString(path);
        runSql(SqlScriptsHelper.parseSqlScript(createTableSql));
    }

    @Override
    public void initData(){
        String resourceFolder = testSuite.getResourceFolder();
        String path = String.format("benchmarks/%s/init_data_postgres.sql", resourceFolder);
        String createTableSql = ResourceUtil.getClasspathResourceAsString(path);
        runSql(SqlScriptsHelper.parseSqlScript(createTableSql));
    }


    @Override
    public void start() {
        this.monitor.start();
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            List<Future<?>> futures = new ArrayList<>();
            // Submit test tasks
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(new BenchmarkTask(this, autoCommit
                    , i, transactionsPerThread, testSuite
                    , userDefinedOperationSupplier, OperationStrategy.SEQUENCE
                )));
            }
            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                    this.monitor.threadSuccess();
                } catch (ExecutionException | InterruptedException e) {
                    logger.error("Test task execution error: {}", e.getCause().getMessage());
                    this.monitor.threadError();
                }
            }
            this.monitor.finish();
            // Shutdown thread pool
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void stop() {
        this.stopFlag = true;
    }

    @Override
    public void cleanup() {
        String resourceFolder = testSuite.getResourceFolder();
        String path = String.format("benchmarks/%s/cleanup.sql", resourceFolder);
        String cleanupSql = ResourceUtil.getClasspathResourceAsString(path);
        runSql(SqlScriptsHelper.parseSqlScript(cleanupSql));
    }

    protected void runSql(List<String> sqlList){
        Connection conn = null;
        try {
            conn = this.getConnectionSupplier().get();
            Statement stmt = conn.createStatement();
            for (String sql : sqlList) {
                stmt.execute(sql);
            }
            stmt.close();
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            logger.error("Error running sql: ", e);
        }
    }


    protected BenchmarkTestImpl(boolean autoCommit
        , int threadCount
        , TestSuite testSuite, int transactionsPerThread
        , Supplier<Connection> connectionSupplier
        , Supplier<TransactionOperation> userDefinedOperationSupplier) {
        this.autoCommit = autoCommit;
        this.threadCount = threadCount;
        this.testSuite = testSuite;
        this.transactionsPerThread = transactionsPerThread;
        this.connectionSupplier = connectionSupplier;
        this.userDefinedOperationSupplier = userDefinedOperationSupplier;
        if (this.testSuite == TestSuite.USER_DEFINED) {
            if (Objects.isNull(this.userDefinedOperationSupplier) || Objects.isNull(this.userDefinedOperationSupplier.get())) {
                throw new IllegalArgumentException("User defined operation cannot be null or empty for user define test suite");
            }
        }
        this.monitor = new BenchmarkMonitor(this);
    }

    protected BenchmarkTestImpl() {
        this(true, DEFAULT_THREAD_COUNT,  DEFAULT_TEST_SUITE
            , DEFAULT_TRANSACTIONS_PER_THREAD, DEFAULT_CONNECTION_SUPPLIER, null);
    }



    public BenchmarkMonitor getMonitor() {
        return monitor;
    }

    public boolean isPrintDetails() {
        return printDetails;
    }

    @Override
    public boolean isAutoCommit() {
        return autoCommit;
    }

    @Override
    public int getThreadCount() {
        return threadCount;
    }

    public TestSuite getTestSuite() {
        return testSuite;
    }

    public int getTransactionsPerThread() {
        return transactionsPerThread;
    }

    @Override
    public Supplier<Connection> getConnectionSupplier() {
        return connectionSupplier;
    }

    public Supplier<TransactionOperation> getUserDefinedOperationSupplier() {
        return userDefinedOperationSupplier;
    }

    private volatile boolean stopFlag = false;
    private final BenchmarkMonitor monitor;
    private final boolean printDetails = true;
    //This is control if the transaction support
    private final boolean autoCommit;
    private final int threadCount;
    private final TestSuite testSuite;
    private final int transactionsPerThread;
    private final Supplier<Connection> connectionSupplier;
    private final Supplier<TransactionOperation> userDefinedOperationSupplier;


    private static final TestSuite DEFAULT_TEST_SUITE = TestSuite.SIMPLE;
    private static final int DEFAULT_THREAD_COUNT = 1;
    private static final Supplier<Connection> DEFAULT_CONNECTION_SUPPLIER = () -> null;
    private static final int DEFAULT_TRANSACTIONS_PER_THREAD = 100;

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkTestImpl.class);

}
