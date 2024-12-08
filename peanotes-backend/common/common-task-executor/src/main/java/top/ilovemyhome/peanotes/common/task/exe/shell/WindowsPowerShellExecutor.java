package top.ilovemyhome.peanotes.common.task.exe.shell;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_16LE;

public final class WindowsPowerShellExecutor extends AbstractOsShellExecutor {

    private static final String POWER_SHELL_CMD = "powershell.exe";
    private static final String PARAM_QUOTE = "\"";


    WindowsPowerShellExecutor(Path tempPath) {
        super(tempPath, POWER_SHELL_CMD, PARAM_QUOTE);
    }

    WindowsPowerShellExecutor(Path tempPath, Path outputPath, Path errorPath) {
        super(tempPath, POWER_SHELL_CMD, PARAM_QUOTE, outputPath, errorPath);
    }

    @Override
    public ExecutionResult execute(String command) {
        String encodedCommand = BASE64_ENCODER.encodeToString(command.getBytes(UTF_16LE));
        return execute(createProcessBuilder(Arrays.asList(this.executionCommand, "-EncodedCommand", encodedCommand)));
    }

    @Override
    public ExecutionResult execute(Path script, Map<String, String> arguments) {
        List<String> commandLine = new ArrayList<>(Arrays.asList(this.executionCommand, "-File", script.toString()));
        commandLine.addAll(arguments.entrySet()
            .stream()
            .flatMap(entry -> Stream.of("-" + entry.getKey(), this.paramQuote + entry.getValue() + this.paramQuote))
            .collect(Collectors.toList()));
        return execute(createProcessBuilder(commandLine));
    }

    @Override
    public ExecutionResult execute(Path script, List<String> arguments) {
        List<String> commandLine = new ArrayList<>(Arrays.asList(this.executionCommand, "-File", script.toString()));
        commandLine.addAll(arguments);
        return execute(createProcessBuilder(commandLine));
    }


    WindowsPowerShellExecutor(Path tempPath, String executionCommand) {
        super(tempPath, executionCommand, PARAM_QUOTE);
    }

}
