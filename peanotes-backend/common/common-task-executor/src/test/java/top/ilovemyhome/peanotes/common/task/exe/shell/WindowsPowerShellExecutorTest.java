package top.ilovemyhome.peanotes.common.task.exe.shell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.condition.OS.LINUX;
import static org.junit.jupiter.api.condition.OS.WINDOWS;
import static top.ilovemyhome.peanotes.common.task.exe.shell.ShellExecutor.BASE64_ENCODER;

@EnabledOnOs({WINDOWS})
public class WindowsPowerShellExecutorTest {

    @TempDir
    private static Path tempDir;

    @Test
    void instance() {
        ShellExecutor instance = ShellExecutor.instance();
        assertThat(instance).isInstanceOf(WindowsPowerShellExecutor.class);
    }

    @Test
    public void testExecuteCommandNonLogPath() {
        String command = "Get-ChildItem Env: | Sort Name";
        WindowsPowerShellExecutor shellExecutor = (WindowsPowerShellExecutor) ShellExecutor.instance(tempDir);
        ExecutionResult executionResult = shellExecutor.execute(command);
        assertThat((executionResult.getReturnCode())).isEqualTo(0);
        assertThat(executionResult.getStandardOutput()).isNotEmpty();
        System.out.println(executionResult.getStandardOutput());
        System.out.println((executionResult.getErrorOutput()));
    }

    @Test
    public void testExecuteCommandWithLogPath() throws IOException {
        String command = "Get-ChildItem Env: | Sort Name";
        Path outputPath = tempDir.resolve("output.log");
        Path errorPath = tempDir.resolve("error.log");
        System.out.println(outputPath);
        System.out.println(errorPath);

        WindowsPowerShellExecutor shellExecutor = (WindowsPowerShellExecutor)
            ShellExecutor.instance(tempDir, outputPath, errorPath);
        ExecutionResult executionResult = shellExecutor.execute(command);
        assertThat((executionResult.getReturnCode())).isEqualTo(0);
        assertThat(executionResult.getStandardOutputFile()).isEqualTo(outputPath);
        assertThat(executionResult.getErrorOutputFile()).isEqualTo(errorPath);

        String outputLog = Files.readString(outputPath);
        System.out.println(outputLog);
        assertThat(outputLog).isNotEmpty();

        assertThat(executionResult.getStandardOutput()).isNull();
        assertThat(executionResult.getErrorOutput()).isNull();
    }

}
