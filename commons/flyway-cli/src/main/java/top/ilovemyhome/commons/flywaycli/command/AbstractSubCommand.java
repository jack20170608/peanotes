package top.ilovemyhome.commons.flywaycli.command;

import org.slf4j.Logger;
import picocli.CommandLine;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractSubCommand {



    public AbstractSubCommand(FlywayConfig config) {
        this.flywayConfig = config;
    }

    @CommandLine.Option(
        names = {"-w", "--work-dir"},
        paramLabel = "<workDir>",
        description = "The working directory for the Flyway migrations.",
        defaultValue = "",
        required = true
    )
    protected String workDir;

    @CommandLine.Option(
        names = {"-e", "--env"},
        defaultValue = "dev",
        paramLabel = "<env>",
        description = "The file for Flyway.",
        required = true
    )
    protected String env;


    @CommandLine.Option(
        names = {"-u", "--url"},
        defaultValue = "jdbc:postgresql://localhost:5432/postgres",
        paramLabel = "<url>",
        description = "The database url for postgres.",
        required = true
    )
    protected String url;

    @CommandLine.Option(
        names = {"-p", "--password"},
        defaultValue = "ignore",
        paramLabel = "<password>",
        description = "The password for postgres.",
        required = true
    )
    protected String password;

    @CommandLine.Option(
        names = {"-U", "--user"},
        defaultValue = "postgres",
        paramLabel = "<user>",
        description = "The user for postgres.",
        required = true
    )
    protected String user;

    @CommandLine.Option(
        names = {"-D", "--driver"},
        defaultValue = "org.postgresql.Driver",
        paramLabel = "<driver>",
        description = "The driver class for postgres.",
        required = true
    )
    protected String driverClass;

    @CommandLine.Option(
        names = {"-s", "--schema"},
        defaultValue = "public",
        paramLabel = "<schemas>",
        description = "The schemas for postgres.",
        required = true
    )
    protected String schema;

    @CommandLine.Option(
        names = {"-t", "--table"},
        defaultValue = "flyway_schema_history",
        paramLabel = "<table>",
        description = "The table for Flyway.",
        required = true
    )
    protected String table;

    @CommandLine.Option(
        names = {"-l", "--locations"},
        defaultValue = "filesystem:db/migration",
        paramLabel = "<locations>",
        description = "The locations for Flyway.",
        required = true
    )
    protected String[] locations;

    @CommandLine.Option(
        names = {"-P", "--placeholders"},
        paramLabel = "<placeholders>",
        description = "Place holders for Flyway SQL scripts."
    )
    protected Map<String, String> placeholders;

    private FlywayConfig flywayConfig;

    protected void logAllOptions(Logger logger) {
        logger.info("workDir: {}", workDir);
        logger.info("env: {}", env);
        logger.info("url: {}", url);
        logger.info("password: {}", password);
        logger.info("user: {}", user);
        logger.info("driverClass: {}", driverClass);
        logger.info("schema: {}", schema);
        logger.info("table: {}", table);
        logger.info("locations: {}", Objects.nonNull(locations) ? String.join(",", locations) : null);
        logger.info("placeholders: {}", placeholders);
    }

    protected FlywayConfig getFlywayConfig() {
        return Objects.nonNull(flywayConfig) ? flywayConfig : new FlywayConfig(
            workDir,
            env,
            driverClass,
            url,
            user,
            password,
            locations,
            schema,
            table,
            Optional.ofNullable(placeholders).orElse(Map.of()),
            null
        );
    }

    public AbstractSubCommand() {}
}
