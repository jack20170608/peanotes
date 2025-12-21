package top.ilovemyhome.commons.database.pool;

import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class HikariDataSourceFactory {

    //eager init
    public static synchronized HikariDataSourceFactory getInstance(Config config){
        if (null == instance){
            instance = new HikariDataSourceFactory();
            instance.init(config);
        }
        return instance;
    }

    private void init(Config config){
        logger.info("Initial database factory");
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
        hikariConfig.setConnectionTimeout(config.getDuration("database.pool.connection_timeout").toMillis());
        hikariConfig.setValidationTimeout(config.getDuration("database.pool.validation_timeout").toMillis());
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setInitializationFailTimeout(config.getDuration("database.pool.initialization_fail_timeout").toMillis());
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setPoolName(config.getString("database.pool.name"));
        hikariConfig.setMaximumPoolSize(config.getInt("database.pool.max_pool_size"));
        hikariConfig.setMinimumIdle(config.getInt("database.pool.min_pool_size"));

        this.hikariDataSource = new HikariDataSource(hikariConfig);
        this.jdbi = Jdbi.create(hikariDataSource);
        logger.info("JDBI init successfully.");

    }

    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    public Jdbi getJdbi() {
        return jdbi;
    }

    private static final Logger logger = LoggerFactory.getLogger(HikariDataSourceFactory.class);

    private static HikariDataSourceFactory instance;

    private HikariDataSource hikariDataSource;

    private Jdbi jdbi;

    private HikariDataSourceFactory() {}
}
