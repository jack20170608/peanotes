package top.ilovemyhome.issue.analysis.dbconnpool.benchmark.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkTest;
import top.ilovemyhome.issue.analysis.dbconnpool.common.testsuite.AbstractTransactionOp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

import static top.ilovemyhome.issue.analysis.dbconnpool.common.SharedResources.getRandomId;


public class QueryOp extends AbstractTransactionOp {

    public QueryOp(BenchmarkTest benchmarkTest) {
        super(benchmarkTest);
    }

    @Override
    public void execute(Connection connection, boolean closeConnection) {
        Objects.requireNonNull(connection, "Connection cannot be null");
        long start = System.currentTimeMillis();
        try (PreparedStatement pstmt = connection.prepareStatement(
            "SELECT id, data, value, created_at FROM benchmark_test WHERE id = ?")) {
            pstmt.setLong(1, getRandomId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Just read, no processing
                    logger.info("Query result: id = {}, data = {}, value = {}, created_at = {}",
                        rs.getLong("id")
                        , rs.getString("data")
                        , rs.getInt("value")
                        , rs.getTimestamp("created_at")
                    );
                }
            }
            long time = System.currentTimeMillis() - start;
            benchmarkTest.getMonitor().commit(time);
            logger.info("Thread {} query time: {} ms", Thread.currentThread().threadId(), time);

        } catch (Exception e) {
            logger.error("Error querying test table: ", e);
        } finally {
            if (closeConnection) {
                closeConnection(connection);
            }
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(QueryOp.class);
}
