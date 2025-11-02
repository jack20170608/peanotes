package top.ilovemyhome.issue.analysis.dbconnpool.common.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkTest;
import top.ilovemyhome.issue.analysis.dbconnpool.common.SharedResources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Objects;

import static top.ilovemyhome.issue.analysis.dbconnpool.common.SharedResources.getRandomId;

public class DeleteOperation extends AbstractDbOperation{

    public DeleteOperation(BenchmarkTest benchmarkTest) {
        super(benchmarkTest);
    }

    @Override
    public int execute(Connection connection, boolean closeConnection) {
        Objects.requireNonNull( connection, "Connection cannot be null");
        int result;
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
            benchmarkTest.successDelete( time, 1);
            result = 1;
            logger.info("Thread {} delete time: {} ms", threadId, time);
        }catch (Exception e) {
            logger.error("Error deleting data.");
            throw new RuntimeException(e);
        }finally {
            if(closeConnection){
                closeConnection(connection);
            }
        }
        return result;
    }

    private static final Logger logger = LoggerFactory.getLogger(DeleteOperation.class);

}
