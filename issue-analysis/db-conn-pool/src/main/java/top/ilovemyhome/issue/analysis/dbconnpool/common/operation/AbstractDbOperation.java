package top.ilovemyhome.issue.analysis.dbconnpool.common.operation;

import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkTest;

public abstract class AbstractDbOperation implements DbOperation{

    protected BenchmarkTest benchmarkTest;

    public AbstractDbOperation(BenchmarkTest benchmarkTest) {
        this.benchmarkTest = benchmarkTest;
    }
}
