package top.ilovemyhome.issue.analysis.dbconnpool.hikaricp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkTest;
import top.ilovemyhome.issue.analysis.dbconnpool.common.enums.OperationStrategy;

import java.sql.SQLException;
import java.util.logging.Logger;

public class SimpleUsageTest {

    public static void main(String[] args) {
        boolean autoCommit = true;
        HikariDataSource dataSource = initHikariDataSource(autoCommit);
        var test = BenchmarkTest.builder()
            .withTransactionsPerThread(10)
            .withOperationsPerTransaction(3)
            .withThreadCount(10)
            .withAutoCommit(true)
            .withOperationStrategy(OperationStrategy.ALL_RANDOM)
            .withConnectionSupplier(() -> {
                try {
                    return dataSource.getConnection();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            })
            .build();
        test.run();
        test.printResults();
    }

    private static HikariDataSource initHikariDataSource(boolean autoCommit) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setUsername("jack");
        hikariConfig.setPassword("1");
        //disable the transaction support
        hikariConfig.setAutoCommit(autoCommit);
        hikariConfig.setValidationTimeout(10000L);
//        hikariConfig.setConnectionTestQuery("SELECT 1 ");
        hikariConfig.setInitializationFailTimeout(120000L);
        hikariConfig.setJdbcUrl("jdbc:postgresql://172.16.10.20:5432/peanotes");
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setPoolName("db-pool");
        return new HikariDataSource(hikariConfig);
    }


    private static final Logger logger = Logger.getLogger(SimpleUsageTest.class.getName());
}
