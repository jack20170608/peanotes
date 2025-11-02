package top.ilovemyhome.issue.analysis.dbconnpool.common.operation;

import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkTest;

public class DbOperationHelper {

    public static DbOperation getDbOperation(BenchmarkTest benchmarkTest, OperationType operationType){
        DbOperation result;
        switch (operationType){
            case INSERT -> result = new InsertOperation(benchmarkTest);
            case QUERY -> result = new QueryOperation(benchmarkTest);
            case UPDATE -> result = new UpdateOperation(benchmarkTest);
            case DELETE -> result = new DeleteOperation(benchmarkTest);
            default -> throw new IllegalArgumentException("Invalid operation type: " + operationType);
        }
        return result;
    }
}
