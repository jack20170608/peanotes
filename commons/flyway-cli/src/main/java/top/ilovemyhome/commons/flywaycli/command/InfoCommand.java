package top.ilovemyhome.commons.flywaycli.command;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import top.ilovemyhome.commons.flywaycli.FlywayBuilder;

import java.util.concurrent.Callable;
import java.util.stream.Stream;

@CommandLine.Command(
    name = "info",
    mixinStandardHelpOptions = true,
    version = "1.0",
    description = "Show the current status of the database migrations."
)
public class InfoCommand  extends AbstractSubCommand implements Callable<Integer>{

    public InfoCommand() {
    }

    public InfoCommand(FlywayConfig config) {
        super(config);
    }

    @Override
    public Integer call() {
        try {
            logger.info("===============================================");
            logAllOptions(logger);
            logger.info("===============================================");
            Flyway flyway = FlywayBuilder.build(getFlywayConfig());
            MigrationInfoService migrationInfoService = flyway.info();
            Stream.of(migrationInfoService.all()).forEach(info -> {
                logger.info("Migration: {} - Version: {} - Description: {} - State: {}",
                    info.getScript(), info.getVersion(), info.getDescription(), info.getState());
            });
        }catch (Throwable t){
            logger.error("Error while executing info command", t);
            CommandLine.usage(this, System.err);
            return 1;
        }
        return 0;
    }



    private static final Logger logger = LoggerFactory.getLogger(InfoCommand.class);
}
