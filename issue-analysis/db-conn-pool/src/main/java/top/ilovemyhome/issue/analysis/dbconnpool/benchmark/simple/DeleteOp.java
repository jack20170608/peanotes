package top.ilovemyhome.issue.analysis.dbconnpool.benchmark.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkTest;
import top.ilovemyhome.issue.analysis.dbconnpool.common.SharedResources;
import top.ilovemyhome.issue.analysis.dbconnpool.common.testsuite.AbstractTransactionOp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Objects;

import static top.ilovemyhome.issue.analysis.dbconnpool.common.SharedResources.getRandomId;

public class DeleteOp extends AbstractTransactionOp {

    public DeleteOp(BenchmarkTest benchmarkTest) {
        super(benchmarkTest);
    }

    @Override
    public void execute(Connection connection, boolean closeConnection) {
        Objects.requireNonNull( connection, "Connection cannot be null");
        long start = System.currentTimeMillis();
        String threadId = Thread.currentThread().getName();
        try (PreparedStatement pstmt = connection.prepareStatement(
            "DELETE FROM benchmark_test WHERE id = ?")) {
            Long id = getRandomId();
            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                SharedResources.insertedIds.remove(id);
            }
            long time = System.currentTimeMillis() - start;
            benchmarkTest.getMonitor().commit(time);
            logger.info("Thread {} delete time: {} ms", threadId, time);
        }catch (Exception e) {
            logger.error("Error deleting data.");
            throw new RuntimeException(e);
        }finally {
            if(closeConnection){
                closeConnection(connection);
            }
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(DeleteOp.class);

}
