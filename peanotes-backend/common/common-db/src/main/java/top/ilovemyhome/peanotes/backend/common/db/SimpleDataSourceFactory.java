package top.ilovemyhome.peanotes.backend.common.db;

import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SimpleDataSourceFactory {

    //eager init
    public static synchronized SimpleDataSourceFactory getInstance(Config config){
        if (null == instance){
            instance = new SimpleDataSourceFactory();
            instance.init(config);
        }
        return instance;
    }

    private void init(Config config){
        LOGGER.info("Initial database factory");
        String driverClass = config.getString("database.driver");
        String url = config.getString("database.url");
        String user = config.getString("database.user");
        String password = config.getString("database.password");

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClass);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        //disable the transaction support
        hikariConfig.setAutoCommit(config.getBoolean("database.auto_commit"));
        hikariConfig.setValidationTimeout(10000L);
        hikariConfig.setConnectionTestQuery("SELECT 1 ");
        hikariConfig.setInitializationFailTimeout(120000L);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setPoolName("db-pool");

        this.hikariDataSource = new HikariDataSource(hikariConfig);
        this.jdbi = Jdbi.create(hikariDataSource);
        LOGGER.info("JDBI init successfully.");

    }

    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    public Jdbi getJdbi() {
        return jdbi;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDataSourceFactory.class);

    private static SimpleDataSourceFactory instance;

    private HikariDataSource hikariDataSource;

    private Jdbi jdbi;

    private SimpleDataSourceFactory() {}
}
