package top.ilovemyhome.commons.flywaycli.command;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.util.OsUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import top.ilovemyhome.commons.flywaycli.FlywayBuilder;
import top.ilovemyhome.commons.flywaycli.command.FlywayConfig;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FlywayBuilderTest {

    @Test
    void buildTest1() {
        FlywayConfig config = new FlywayConfig(
            "",
            "dev",
            "org.postgresql.Driver",
            "jdbc:postgresql://localhost:5432/test",
            "user",
            "pass",
            null,
            "public",
            "flyway_schema_history",
            Map.of("key", "value"),
            null
        );

        Flyway flyway = FlywayBuilder.build(config);
        Configuration configuration = flyway.getConfiguration();
        assertThat(configuration.getWorkingDirectory()).isEmpty();
        assertThat(configuration.getUrl()).isEqualTo("jdbc:postgresql://localhost:5432/test");
        assertThat(configuration.getUser()).isEqualTo("user");
        assertThat(configuration.getPassword()).isEqualTo("pass");
        assertThat(configuration.getDriver()).isEqualTo("org.postgresql.Driver");
        assertThat(configuration.getSchemas()).containsExactly("public");
        assertThat(configuration.getTable()).isEqualTo("flyway_schema_history");
        assertThat(configuration.getPlaceholders()).isEqualTo(Map.of("key", "value"));
        assertThat(configuration.getLocations()).isEqualTo(new Location[]{
            new Location("classpath:db/migration")
            ,new Location("filesystem:db/migration")
        });
    }


    @Test
    void buildTest2() {
        FlywayConfig config = new FlywayConfig(
            OsUtils.isWindows() ? "c:\\temp\\flyway-cli" :"/appvol/global1/flyway-cli",
            "dev",
            "org.postgresql.Driver",
            "jdbc:postgresql://localhost:5432/postgres",
            "user",
            "pass",
            null,
            "public",
            "flyway_schema_history",
            Map.of("key", "value"),
            null
        );

        Flyway flyway = FlywayBuilder.build(config);
        Configuration configuration = flyway.getConfiguration();
        if (OsUtils.isWindows()){
            assertThat(configuration.getWorkingDirectory()).isEqualTo("c:\\temp\\flyway-cli");
        } else {
            assertThat(configuration.getWorkingDirectory()).isEqualTo("/appvol/global1/flyway-cli");
        }
        assertThat(configuration.getLocations()).isEqualTo(new Location[]{
            new Location("classpath:db/migration")
            ,new Location("filesystem:db/migration")
        });
        assertThat(configuration.getUrl()).isEqualTo("jdbc:postgresql://localhost:5432/postgres");
        assertThat(configuration.getUser()).isEqualTo("user");
        assertThat(configuration.getPassword()).isEqualTo("pass");
        assertThat(configuration.getDriver()).isEqualTo("org.postgresql.Driver");
        assertThat(configuration.getSchemas()).containsExactly("public");
        assertThat(configuration.getTable()).isEqualTo("flyway_schema_history");
        assertThat(configuration.getPlaceholders()).isEqualTo(Map.of("key", "value"));
    }
}
