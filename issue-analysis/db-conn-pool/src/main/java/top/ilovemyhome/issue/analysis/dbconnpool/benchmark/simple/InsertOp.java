package top.ilovemyhome.issue.analysis.dbconnpool.benchmark.simple;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkTest;
import top.ilovemyhome.issue.analysis.dbconnpool.common.SharedResources;
import top.ilovemyhome.issue.analysis.dbconnpool.common.testsuite.AbstractTransactionOp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;

public class InsertOp extends AbstractTransactionOp {

    public InsertOp(BenchmarkTest benchmarkTest) {
        super(benchmarkTest);
    }

    @Override
    public void execute(Connection connection, boolean closeConnection) {
        Objects.requireNonNull( connection, "Connection cannot be null");
        long start = System.currentTimeMillis();
        String threadId = Thread.currentThread().getName();
        String data = "test_data_" + threadId + "_" + System.nanoTime();
        int value = RandomUtils.secure().randomInt(1, 1000000);
        try (PreparedStatement pstmt = connection.prepareStatement(
            "INSERT INTO benchmark_test (data, value) VALUES (?, ?)",
            Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, data);
            pstmt.setInt(2, value);
            pstmt.executeUpdate();
            // Get generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    SharedResources.insertedIds.add(id);
                    logger.info("Generated ID: {}", id);
                }
            }
            long time = System.currentTimeMillis() - start;
            benchmarkTest.getMonitor().commit(time);
            logger.info("Thread {} insert time: {} ms", threadId, time);
        }catch (Exception e) {
            logger.error("Error inserting data: ", e);
        }finally {
            if(closeConnection){
                closeConnection(connection);
            }
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(InsertOp.class);

}
