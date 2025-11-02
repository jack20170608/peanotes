package top.ilovemyhome.issue.analysis.dbconnpool.common.operation;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Objects;
import java.util.function.Supplier;

import static top.ilovemyhome.issue.analysis.dbconnpool.common.SharedResources.getRandomId;

public class UpdateOperation extends AbstractDbOperation {

    public UpdateOperation(BenchmarkTest benchmarkTest) {
        super(benchmarkTest);
    }

    @Override
    public int execute(Connection connection, boolean closeConnection) {
        Objects.requireNonNull(connection, "Connection cannot be null");
        int result;
        long start = System.currentTimeMillis();
        String threadId = Thread.currentThread().getName();
        String newData = "updated_data_" + threadId + "_" + System.nanoTime();
        int newValue = RandomUtils.secure().randomInt(1, 1000000);
        try (PreparedStatement pstmt = connection.prepareStatement(
            "UPDATE benchmark_test SET data = ?, value = ? WHERE id = ?")) {
            pstmt.setString(1, newData);
            pstmt.setInt(2, newValue);
            pstmt.setLong(3, getRandomId());
            pstmt.executeUpdate();

            long time = System.currentTimeMillis() - start;
            benchmarkTest.successUpdate( time, 1);
            result = 1;
            logger.info("Thread {} update time: {} ms", threadId, time);
        }catch (Exception e) {
            result = 0;
            logger.error("Error updating data: ", e);
        }finally {
            if(closeConnection){
                closeConnection(connection);
            }
        }
        return result;
    }

    private static final Logger logger = LoggerFactory.getLogger(UpdateOperation.class);

}
