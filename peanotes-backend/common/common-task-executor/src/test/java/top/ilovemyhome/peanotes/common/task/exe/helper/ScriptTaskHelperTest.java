package top.ilovemyhome.peanotes.common.task.exe.helper;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled
public class ScriptTaskHelperTest {

    @TempDir
    private static Path ROOT_PATH;

    private static Path BIN_PATH;
    private static Path LOG_PATH;

    private static Path shellScriptPath;
    private static String shellCmd;

    @BeforeAll
    public static void beforeAll() throws IOException {
        BIN_PATH = ROOT_PATH.resolve("bin");
        LOG_PATH = ROOT_PATH.resolve("log");
        Files.createDirectories(BIN_PATH);
        Files.createDirectories(LOG_PATH);

        if (SystemUtils.IS_OS_WINDOWS) {
            shellScriptPath = BIN_PATH.resolve("shell.ps1");
            shellCmd = "powershell";
        } else if (SystemUtils.IS_OS_LINUX) {
            shellScriptPath = BIN_PATH.resolve("shell.sh");
            shellCmd = "/usr/bin/bash";
        } else {
            throw new IllegalStateException("Os not supported");
        }
        Files.writeString(shellScriptPath, """
            java -version
            """);
    }

    @Test
    public void testSomeCommand() throws IOException {
        Path logFile = LOG_PATH.resolve("shell.log");
        int returnCode = ScriptTaskHelper.execToFile(shellCmd, shellScriptPath, logFile);
        assertThat(returnCode).isEqualTo(0);
        System.out.println(Files.readString(logFile, StandardCharsets.UTF_8));
    }

    @Test
    public void testPathResolve() {
        Path rootPath = ROOT_PATH;
        System.out.println(rootPath.resolve("bin/a/b/c"));


        System.out.println("OS Name: " + System.getProperty("os.name"));
    }
}
