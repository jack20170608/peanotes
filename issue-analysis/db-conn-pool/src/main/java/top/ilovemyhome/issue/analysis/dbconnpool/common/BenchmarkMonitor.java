package top.ilovemyhome.issue.analysis.dbconnpool.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.issue.analysis.dbconnpool.benchmark.State;
import top.ilovemyhome.issue.analysis.dbconnpool.util.SplitLineHelper;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BenchmarkMonitor {

    //The whole start/stop time and test result
    private long startTs;
    private long stopTs;
    private long totalTimeCost;
    private State state = State.INIT;
    private boolean success;

    //The thread count success/fail
    private final int totalThreadCount;
    private final AtomicInteger failedThreadCount = new AtomicInteger(0);
    private final AtomicInteger successThreadCount = new AtomicInteger(0);


    private final AtomicLong totalTransactionCount = new AtomicLong(0);
    private final AtomicLong commitTransactionCount = new AtomicLong(0);
    private final AtomicLong rollbackTransactionCount = new AtomicLong(0);
    private final AtomicLong failedTransactionCount = new AtomicLong(0);

    private final AtomicLong commitTotalTime = new AtomicLong(0);
    private final AtomicLong rollbackTotalTime = new AtomicLong(0);

    private final BenchmarkTest benchmarkTest;

    public BenchmarkMonitor(BenchmarkTest benchmarkTest) {
        this.benchmarkTest = benchmarkTest;
        this.totalThreadCount = benchmarkTest.getThreadCount();
    }

    public void start() {
        this.startTs = System.currentTimeMillis();
        this.state = State.RUNNING;
    }

    public void finish() {
        this.stopTs = System.currentTimeMillis();
        this.state = State.DONE;
        this.totalTimeCost = stopTs - startTs;
    }

    public void threadSuccess(){
        this.successThreadCount.incrementAndGet();
    }

    public void threadError(){
        this.failedThreadCount.incrementAndGet();
    }

    public void commit(long timeCost){
        this.commitTransactionCount.incrementAndGet();
        this.commitTotalTime.addAndGet(timeCost);
        this.totalTransactionCount.incrementAndGet();
    }

    public void rollback(long timeCost){
        this.rollbackTransactionCount.incrementAndGet();
        this.rollbackTotalTime.addAndGet(timeCost);
        this.totalTransactionCount.incrementAndGet();
    }

    public void commitFailed(){
        this.failedTransactionCount.incrementAndGet();
        this.totalTransactionCount.incrementAndGet();
    }

    public void printResults() {
        // 计算事务相关统计数据
        long totalTxn = totalTransactionCount.get();
        long commitCount = commitTransactionCount.get();
        long rollbackCount = rollbackTransactionCount.get();
        long failedCount = failedTransactionCount.get();

        double avgCommitTime = commitCount > 0 ? (double) commitTotalTime.get() / commitCount : 0;
        double avgRollbackTime = rollbackCount > 0 ? (double) rollbackTotalTime.get() / rollbackCount : 0;
        double txnThroughput = totalTxn > 0 ? (totalTxn * 1000.0) / totalTimeCost : 0;

        // 计算成功率
        double successRate = totalTxn > 0 ? ((commitCount + rollbackCount) * 100.0) / totalTxn : 100;

        // 打印标题
        logger.info(SplitLineHelper.getTitleSeparator("BENCHMARK RESULTS"));
        logger.info("======================================================");

        // 打印配置信息
        logger.info(String.format("%-35s: %s", "Connection Mode", this.benchmarkTest.isAutoCommit() ? "Auto-Commit" : "Transaction"));
        logger.info(String.format("%-35s: %d", "Thread Count", this.totalThreadCount));
        logger.info(String.format("%-35s: %s", "Test State", this.state));
        logger.info(String.format("%-35s: %s", "Test Successful", this.success));

        // 打印总体统计
        logger.info("------------------------------------------------------");
        logger.info(String.format("%-35s: %d ms", "Total Execution Time", this.totalTimeCost));
        logger.info(String.format("%-35s: %d", "Successful Threads", this.successThreadCount.get()));
        logger.info(String.format("%-35s: %d", "Failed Threads", this.failedThreadCount.get()));

        // 打印事务统计
        logger.info("------------------------------------------------------");
        logger.info(String.format("%-35s: %d", "Total Transactions", totalTxn));
        logger.info(String.format("%-35s: %d", "Committed Transactions", commitCount));
        logger.info(String.format("%-35s: %d", "Rolled Back Transactions", rollbackCount));
        logger.info(String.format("%-35s: %d", "Failed Transactions", failedCount));
        logger.info(String.format("%-35s: %.2f%%", "Success Rate", successRate));

        // 打印性能统计
        logger.info("------------------------------------------------------");
        logger.info(String.format("%-35s: %.2f txn/sec", "Transaction Throughput", txnThroughput));
        logger.info(String.format("%-35s: %.2f ms", "Avg. Commit Time", avgCommitTime));
        logger.info(String.format("%-35s: %.2f ms", "Avg. Rollback Time", avgRollbackTime));

        // 打印结束线
        logger.info("======================================================");
    }



    private static final Logger logger = LoggerFactory.getLogger(BenchmarkMonitor.class);
}
