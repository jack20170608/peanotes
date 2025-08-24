package top.ilovemyhome.commons.flywaycli.command;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import top.ilovemyhome.commons.flywaycli.FlywayBuilder;

import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "migration",
    mixinStandardHelpOptions = true,
    version = "1.0",
    description = "Do the database migrations."
)
public class MigrateCommand extends AbstractSubCommand implements Callable<Integer>{

    public MigrateCommand() {
    }

    public MigrateCommand(FlywayConfig config) {
        super(config);
    }

    @Override
    public Integer call() {
        try {
            logger.info("===============================================");
            logAllOptions(logger);
            logger.info("===============================================");
            Flyway flyway = FlywayBuilder.build(getFlywayConfig());
            flyway.baseline();
            flyway.repair();
            MigrateResult migrateResult = flyway.migrate();
            migrateResult.migrations.forEach(migration -> {
                logger.info("Migration: {} - Version: {} - Description: {} - ExecutionTime: {}",
                    migration.filepath, migration.version, migration.description, migration.executionTime);
            });
        }catch (Throwable t){
            logger.error("Error while executing info command", t);
            CommandLine.usage(this, System.err);
            return 1;
        }
        return 0;
    }

    private static final Logger logger = LoggerFactory.getLogger(MigrateCommand.class);
}
