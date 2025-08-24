package top.ilovemyhome.commons.flywaycli.command;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import top.ilovemyhome.commons.flywaycli.SharedResources;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

public class InfoCommandTest {

    static DataSource dataSource;
    static String user = "postgres";
    static String database = "postgres";

    @BeforeAll
    public static void setUp() throws Exception {
        dataSource = SharedResources.getDataSource(user, database);
    }

    @AfterAll
    public static void tearDown() throws Exception {
        SharedResources.closePg();
    }

    @Test
    public void testInfoCommand() throws Exception {
        Connection connection = dataSource.getConnection(user, null);
        System.out.println(connection.getMetaData().getURL());
        FlywayConfig config = new FlywayConfig(
            "",
            "dev",
            "org.postgresql.Driver",
            connection.getMetaData().getURL(),
            user,
            null,
            null,
            "public",
            "flyway_schema_history",
            Map.of("key", "value"),
            null
        );
        InfoCommand infoCommand = new InfoCommand(config);
        infoCommand.call();
        MigrateCommand migrateCommand= new MigrateCommand(config);
        migrateCommand.call();
    }


}
