package top.ilovemyhome.peanotes.backend.common.db;

import com.typesafe.config.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.config.ConfigLoader;

import java.sql.*;
import java.util.Objects;

@Disabled
public class FlyWayHelperTest {

    @BeforeAll
    public static void initConfig(){
        config = ConfigLoader.loadConfig("config/common-db.conf"
            , "config/common-db-test.conf");
    }

    @Test
    public void testDbConnectivity(){
        String driver = config.getString("database.driver");
        String jdbcUrl = config.getString("database.url");
        String user = config.getString("database.user");
        String password = config.getString("database.password");
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(jdbcUrl, user, password);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select version() as version ;");
            String version = null;
            while (rs.next()){
                version = rs.getString("version");
            }
            LOGGER.info("Version is [{}].", version);
        }catch (Exception e){
            LOGGER.error("DB connection failure.", e);
        }finally {
            if (Objects.nonNull(connection)){
                try {
                    connection.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }


    @Test
    public void testFlywayRun(){
        FlyWayHelper.run(config);
    }


    private static Config config;
    private static final Logger LOGGER = LoggerFactory.getLogger(FlyWayHelperTest.class);
}
