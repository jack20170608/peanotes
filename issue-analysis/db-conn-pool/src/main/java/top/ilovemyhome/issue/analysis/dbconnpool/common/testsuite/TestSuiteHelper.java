package top.ilovemyhome.issue.analysis.dbconnpool.common.testsuite;

import top.ilovemyhome.issue.analysis.dbconnpool.benchmark.tpca.AccountTransferOp;
import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkTask;
import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkTest;
import top.ilovemyhome.issue.analysis.dbconnpool.benchmark.simple.DeleteOp;
import top.ilovemyhome.issue.analysis.dbconnpool.benchmark.simple.InsertOp;
import top.ilovemyhome.issue.analysis.dbconnpool.benchmark.simple.QueryOp;
import top.ilovemyhome.issue.analysis.dbconnpool.benchmark.simple.UpdateOp;
import top.ilovemyhome.peanotes.commons.text.ThreadSafeRandomGenerator;

import java.util.Map;

public class TestSuiteHelper {

    public static TransactionOperation getTransactionOperation(BenchmarkTask task) {
        TransactionOperation result = null;
        BenchmarkTest test = task.getBenchmarkTest();
        TestSuite testSuite = task.getTestSuite();
        switch (testSuite) {
            case SIMPLE -> {
                //50% insert
                //25% update
                //15% query
                //10% delete
                Class<? extends TransactionOperation> clazz = ThreadSafeRandomGenerator.getRandomTypeByPercentage(
                    Map.of(InsertOp.class, 50,
                        UpdateOp.class, 25,
                        QueryOp.class, 15,
                        DeleteOp.class, 10
                    )
                );
            }
            case TPC_C -> {
                throw new UnsupportedOperationException("TPC-C is not supported yet.");
            }
            case TPC_A -> {
                result = new AccountTransferOp(test);
            }
            case USER_DEFINED ->
                result = task.getUserDefinedOperationSupplier().get();
        }
        ;
        return result;
    }
}
