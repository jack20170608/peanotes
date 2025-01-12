package top.ilovemyhome.peanotes.common.task.exe.handler.shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public final class LinuxBashShellExecutor extends AbstractOsShellExecutor {

    private static final String BASH_SHELL_CMD = "bash";
    private static final String PARAM_QUOTE = "\"";

    LinuxBashShellExecutor(Path tempPath) {
        super(tempPath, BASH_SHELL_CMD, PARAM_QUOTE);
    }

    LinuxBashShellExecutor(Path tempPath, Path outputPath, Path errorPath) {
        super(tempPath, BASH_SHELL_CMD, PARAM_QUOTE, outputPath, errorPath);
    }

    //0. Execute command directly
    @Override
    public ExecutionResult execute(String command) {
        return execute(createProcessBuilder(Arrays.asList(executionCommand,"-c" ,command)));
    }

    //1. Execute file script
    @Override
    public ExecutionResult execute(Path script, Map<String, String> arguments) {
        List<String> commandLine = new ArrayList<>(Arrays.asList(this.executionCommand, script.toString()));
        commandLine.addAll(arguments.entrySet()
            .stream()
            .flatMap(entry -> Stream.of("-" + entry.getKey(), this.paramQuote + entry.getValue() + this.paramQuote))
            .collect(Collectors.toList()));
        return execute(createProcessBuilder(commandLine));
    }

    @Override
    public ExecutionResult execute(Path script, List<String> arguments) {
        List<String> commandLine = new ArrayList<>(Arrays.asList(this.executionCommand, script.toString()));
        commandLine.addAll(arguments);
        return execute(createProcessBuilder(commandLine));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxBashShellExecutor.class);
}
