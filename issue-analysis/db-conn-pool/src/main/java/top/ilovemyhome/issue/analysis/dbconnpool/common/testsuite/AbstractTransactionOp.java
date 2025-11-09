package top.ilovemyhome.issue.analysis.dbconnpool.common.testsuite;


import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkTest;

public abstract class AbstractTransactionOp implements TransactionOperation {

    protected final BenchmarkTest benchmarkTest;

    protected AbstractTransactionOp(BenchmarkTest benchmarkTest) {
        this.benchmarkTest = benchmarkTest;
    }
}
