package top.ilovemyhome.peanotes.common.task.exe.handler.shell;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public interface ShellExecutor {

    Base64.Encoder BASE64_ENCODER = Base64.getEncoder();

    String JSE_GLOBBER_THREAD_NAME = "JSE-Gobbler";

    ExecutionResult execute(String command);

    ExecutionResult execute(Path script, Map<String, String> arguments);

    ExecutionResult execute(InputStream script, Map<String, String> arguments);

    ExecutionResult execute(Path script, List<String> arguments);

    ExecutionResult execute(InputStream script, List<String> arguments);

    static ShellExecutor instance() {
        return instance(null);
    }

    static ShellExecutor instance(Path tempPath) {
        return instance(tempPath, null, null);
    }

    static ShellExecutor instance(Path tempPath, Path outputPath, Path errorPath) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("win")) {
            return new WindowsPowerShellExecutor(tempPath, outputPath, errorPath);
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
            return new LinuxBashShellExecutor(tempPath, outputPath, errorPath);
        } else {
            return new UnsupportedOsShellExecutor(osName);
        }
    }
}
