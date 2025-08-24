package top.ilovemyhome.commons.flywaycli;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import top.ilovemyhome.commons.flywaycli.command.FlywayConfig;
import top.ilovemyhome.commons.flywaycli.command.InfoCommand;
import top.ilovemyhome.commons.flywaycli.command.MigrateCommand;

import java.util.Map;

@Disabled
public class End2EndTest {

    @Test
    public void testInfo2() {
        FlywayConfig config = new FlywayConfig(
            "",
            "dev",
            "org.postgresql.Driver",
            "jdbc:postgresql://10.10.10.5:5432/postgres",
            "jack",
            "1",
            null,
            "public",
            "flyway_schema_history",
            Map.of("key", "value"),
            null
        );
        InfoCommand infoCommand = new InfoCommand(config);
        infoCommand.call();
        MigrateCommand migrationCommand = new MigrateCommand(config);
        migrationCommand.call();
        infoCommand.call();
    }
}
