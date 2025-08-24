package top.ilovemyhome.commons.flywaycli;


import org.jdbi.v3.core.statement.Call;
import picocli.CommandLine;
import top.ilovemyhome.commons.flywaycli.command.InfoCommand;
import top.ilovemyhome.commons.flywaycli.command.MigrateCommand;

import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "flyway-cli",
    mixinStandardHelpOptions = true,
    version = "1.0",
    description = "A command line tool for Flyway database migrations.",
    subcommands = {
        MigrateCommand.class,
//        CleanCommand.class,
        InfoCommand.class,
//        ValidateCommand.class,
//        BaselineCommand.class,
//        UndoCommand.class,
//        RepairCommand.class,
//        BaselineVersionCommand.class
        CommandLine.HelpCommand.class
    }

)
public class FlywayCli implements Callable<Integer> {

    public static void main(String[] args) {
        FlywayCli flywayCli = new FlywayCli();
        CommandLine cmd = new CommandLine(flywayCli);
        if (args.length == 0) {
            cmd.usage(System.out);
            System.exit(0);
        }
        cmd.setExecutionStrategy(new CommandLine.RunAll());
        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }


    @Override
    public Integer call() throws Exception {
        return 0;
    }
}
