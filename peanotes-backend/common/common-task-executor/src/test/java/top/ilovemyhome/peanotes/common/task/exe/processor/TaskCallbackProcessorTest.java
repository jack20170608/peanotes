package top.ilovemyhome.peanotes.common.task.exe.processor;

import io.muserver.MuServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import top.ilovemyhome.peanotes.backend.common.utils.NetUtil;
import top.ilovemyhome.peanotes.common.task.exe.TaskExecutor;
import top.ilovemyhome.peanotes.common.task.exe.TaskExecutorContext;
import top.ilovemyhome.peanotes.common.task.exe.common.FooTaskAdminServer;

import java.nio.file.Path;
import java.util.List;

public class TaskCallbackProcessorTest {

    @Test
    public void testCallback(){

    }

    @TempDir
    static Path rootPath;

    @BeforeAll
    public static void startTaskAdminServer() throws Exception {
        FooTaskAdminServer adminServer = new FooTaskAdminServer();
        muServer = adminServer.getMuServer();
        System.out.println(muServer.uri());
        TaskExecutorContext executorContext = TaskExecutorContext.builder()
            .withAppName(appName)
            .withFqdn(fqdn)
            .withPort(port)
            .withListOfAdmin(List.of(muServer.uri().toString()))
            .withRootPath(rootPath)
            .build();
        taskExecutor= TaskExecutor.builder()
            .withTaskExecutorContext(executorContext)
            .build();
        System.out.println(rootPath.toString());
    }

    @AfterAll
    public static void stopTaskAdminServer() throws Exception {
        if (muServer != null) {
            muServer.stop();
        }
    }

    private static final String appName = "foo";
    private static final String fqdn = "localhost";
    private static final int port = NetUtil.findAvailablePort(12580);

    private static TaskExecutor taskExecutor;
    private static MuServer muServer;


}
