package top.ilovemyhome.peanotes.common.task.exe.shell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.io.TempDir;
import top.ilovemyhome.peanotes.common.task.exe.handler.shell.ExecutionResult;
import top.ilovemyhome.peanotes.common.task.exe.handler.shell.ShellExecutor;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.condition.OS.LINUX;

@EnabledOnOs({LINUX})
public class LinuxBashShellExecutorTest {

    @TempDir
    private static Path tempDir;

    @Test
    public void testExecuteCommand() {
        String command = "ping -c 4 -W 1 127.0.0.1";
        ShellExecutor shellExecutor = ShellExecutor.instance(tempDir);
        ExecutionResult executionResult = shellExecutor.execute(command);
        assertThat((executionResult.getReturnCode())).isEqualTo(0);
        assertThat(executionResult.getStandardOutput()).isNotEmpty();
        System.out.println(executionResult.getStandardOutput());
        System.out.println((executionResult.getErrorOutput()));
    }

    @Test
    public void testExecuteScript() throws Exception {
        ShellExecutor shellExecutor = ShellExecutor.instance(tempDir);
        Path fooScript = tempDir.resolve("foo.sh");
        Files.writeString(fooScript, """
            #!/usr/bin/env bash
            id=$1
            name=$2
            echo id=$id, name=$name
            """, StandardCharsets.UTF_8);
        ExecutionResult executionResult = shellExecutor.execute(fooScript, List.of("100", "jack"));
        assertThat((executionResult.getReturnCode())).isEqualTo(0);
        assertThat(executionResult.getStandardOutput()).isNotEmpty();
        System.out.println(executionResult.getStandardOutput());
        System.out.println((executionResult.getErrorOutput()));
    }


    @Test
    public void testExecuteScriptWithLog() throws Exception {
        Path outputPath = tempDir.resolve("output.log");
        Path errorPath = tempDir.resolve("error.log");
        ShellExecutor shellExecutor = ShellExecutor.instance(tempDir, outputPath, errorPath);
        Path fooScript = tempDir.resolve("foo.sh");
        Files.writeString(fooScript, """
            #!/usr/bin/env bash
            id=$1
            name=$2
            echo id=$id, name=$name
            """, StandardCharsets.UTF_8);
        ExecutionResult executionResult = shellExecutor.execute(fooScript, List.of("100", "jack"));
        assertThat((executionResult.getReturnCode())).isEqualTo(0);

        assertThat(executionResult.getStandardOutput()).isNull();
        assertThat(executionResult.getErrorOutput()).isNull();

        assertThat(executionResult.getStandardOutputFile()).isEqualTo(outputPath);
        assertThat(executionResult.getErrorOutputFile()).isEqualTo(errorPath);

        String outputLog = Files.readString(outputPath);
        System.out.println(outputLog);
        assertThat(outputLog).isNotEmpty();

    }
}
