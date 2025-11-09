package top.ilovemyhome.issue.analysis.dbconnpool.benchmark.tpca;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkTest;
import top.ilovemyhome.issue.analysis.dbconnpool.common.testsuite.AbstractTransactionOp;
import top.ilovemyhome.peanotes.commons.text.ThreadSafeRandomGenerator;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import static top.ilovemyhome.issue.analysis.dbconnpool.benchmark.tpca.Config.NUM_OF_TOTAL_ACCOUNTS;

public class AccountTransferOp extends AbstractTransactionOp {

    public AccountTransferOp(BenchmarkTest benchmarkTest) {
        super(benchmarkTest);
    }

    @Override
    public void execute(Connection connection, boolean closeConnection) {
        Objects.requireNonNull(connection, "Connection cannot be null");
        StopWatch sw = new StopWatch();
        sw.start();
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;
        try {
            // 1. debit from the source account
            ps = connection.prepareStatement("""
                UPDATE accounts
                SET acc_balance = acc_balance - ?
                WHERE acc_id = ? AND acc_balance >= ? AND acc_status = 'A'
                RETURNING acc_balance
                """);
            BigDecimal amount = ThreadSafeRandomGenerator
                .generateRandomBigDecimal(BigDecimal.valueOf(1), BigDecimal.valueOf(1000), 2);
            int srcAccId = ThreadSafeRandomGenerator.generateRandomInt(1, NUM_OF_TOTAL_ACCOUNTS);
            int destAccId = ThreadSafeRandomGenerator.generateRandomInt(1, NUM_OF_TOTAL_ACCOUNTS);
            ps.setBigDecimal(1, amount);
            ps.setInt(2, srcAccId);
            ps.setBigDecimal(3, amount);
            rs = ps.executeQuery();
            BigDecimal srcNewBalance = BigDecimal.ZERO;
            if (!rs.next()) {
                throw new SQLException("Not enough balance in account " + srcAccId);
            }
            srcNewBalance = rs.getBigDecimal(1);
            close(rs, ps);

            // 2. credit into the target account
            ps = connection.prepareStatement("""
                UPDATE accounts
                SET acc_balance = acc_balance + ?
                WHERE acc_id = ? AND acc_status = 'A'
                RETURNING acc_balance
            """);
            ps.setBigDecimal(1, amount);
            ps.setInt(2, destAccId);
            rs = ps.executeQuery();
            double destNewBalance = 0;
            if (!rs.next()) {
                throw new SQLException("Account " + destAccId + " is not active");
            }
            destNewBalance = rs.getDouble(1);
            close(rs, ps);

            //3. Insert into history for debit
            ps = connection.prepareStatement("""
                INSERT INTO history (hist_acc_id, hist_amount, hist_type, hist_src_acc_id)
                VALUES (?, ?, 'W', NULL)
            """);
            ps.setInt(1, srcAccId);
            ps.setBigDecimal(2, amount);
            ps.executeUpdate();
            close(ps);

            //4. Insert into history for credit
            ps = connection.prepareStatement("""
                INSERT INTO history (hist_acc_id, hist_amount, hist_type, hist_src_acc_id)
                VALUES (?, ?, 'D', ?)
            """);
            ps.setInt(1, destAccId);
            ps.setBigDecimal(2, amount);
            ps.setInt(3, srcAccId);
            ps.executeUpdate();
            close(ps);
            connection.commit();
            success = true;
            logger.info("Transfer ${} from {} to {} successfully, srcNewBalance: {}, destNewBalance: {}", amount, srcAccId, destAccId, srcNewBalance, destNewBalance);
        } catch (Exception e) {
            try {
                success = false;
                connection.rollback();
            } catch (SQLException se) {
                throw new RuntimeException(se);
            }
            logger.error("Error transferring money.", e);
        } finally {
            if (closeConnection) {
                closeConnection(connection);
            }
            sw.stop();
            long timeCost = sw.getTime();
            if (success) {
                benchmarkTest.getMonitor().commit(timeCost);
            }else {
                benchmarkTest.getMonitor().rollback(timeCost);
            }
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(AccountTransferOp.class);
}
