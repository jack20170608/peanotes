package top.ilovemyhome.peanotes.common.task.exe.shell;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

/**
 * Demo and test application.
 */
public class ShellExecutorTest {

    @Test
    public void testExecutor() {
        ShellExecutor executor = ShellExecutor.instance();
        if (executor instanceof WindowsPowerShellExecutor) {
            System.out.println("Execute command: ");
            String output = executor.execute("Write-Host Hello PowerShell!").getStandardOutput();
            System.out.println(" output = " + output);
            Map<String, String> arguments = Collections.singletonMap("name", "PowerShell");

            System.out.println("Execute script as file: ");
            output = executor.execute(Paths.get(".\\src\\test\\resources\\shell\\test.ps1"), arguments).getStandardOutput();
            System.out.println(" output = " + output);
            System.out.println("Execute script from classpath: ");
            output = executor.execute(ShellExecutorTest.class.getResourceAsStream("/shell/test.ps1"), arguments).getStandardOutput();
            System.out.println(" output = " + output);
        } else if (executor instanceof LinuxBashShellExecutor) {

        } else {
            throw new RuntimeException("Unsupported executor type: " + executor.getClass());
        }
    }
}
