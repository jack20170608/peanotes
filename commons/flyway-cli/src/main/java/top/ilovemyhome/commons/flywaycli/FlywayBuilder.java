package top.ilovemyhome.commons.flywaycli;

import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.slf4j.Logger;
import top.ilovemyhome.commons.flywaycli.command.FlywayConfig;
import org.flywaydb.core.Flyway;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FlywayBuilder {

    public static Flyway build(FlywayConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("FlywayConfig cannot be null");
        }
        logger.info("Building Flyway instance with configuration: {}", config);
        //fallback to load if from classpath
        String[] normalizedLocations = Objects.requireNonNullElse(config.locations(), defaultLocations);
        logger.debug("Normalized locations: {}", String.join(",", normalizedLocations));
        FluentConfiguration conf = Flyway.configure()
            .encoding(StandardCharsets.UTF_8)
            .workingDirectory(config.workDir())
            .baselineOnMigrate(true)
            .baselineVersion("0")
            .validateMigrationNaming(true)
            .driver(config.driverClass())
            .dataSource(config.url(), config.user(), config.password())
            .driver(config.driverClass())
            .locations(normalizedLocations)
            .schemas(config.schema())
            .table(config.table())
            .placeholderReplacement(true)
            .placeholders(config.placeholders());
        if (!Objects.isNull(config.callback())) {
            conf.callbacks(config.callback());
        }
        return conf.load();
    }

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(FlywayBuilder.class);

    private static final String[] defaultLocations = new String[]{
        "classpath:db/migration",
        "filesystem:db/migration"
    };

}
