package top.ilovemyhome.peanotes.common.task.exe.processor;

import io.muserver.MuServer;
import org.apache.commons.lang3.ThreadUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import top.ilovemyhome.peanotes.backend.common.utils.NetUtil;
import top.ilovemyhome.peanotes.common.task.exe.TaskExecutor;
import top.ilovemyhome.peanotes.common.task.exe.TaskExecutorContext;
import top.ilovemyhome.peanotes.common.task.exe.common.FooTaskAdminServer;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RegistryProcessorTest {


    @Test
    public void testRegisterAndUnregister() throws Exception {
        TaskExecutorContext executorContext = TaskExecutorContext.builder()
            .withSchema("http")
            .withAppName(appName)
            .withFqdn(fqdn)
            .withPort(port)
            .withListOfAdmin(List.of(muServer.httpUri().toString()))
            .withRootPath(rootPath)
            .build();
        TaskExecutor taskExecutor = TaskExecutor.of(executorContext);
        System.out.println(executorContext.uri().toString());
        RegistryProcessor processor = RegistryProcessor.builder()
            .withTaskExecutor(taskExecutor)
            .build();
        processor.start();

        int counter = 5;
        while (!processor.isRegistered() && counter-- > 0){
            Thread.sleep(Duration.ofMillis(100));
        }
        assertThat(taskExecutor.getContext().getListOfAdmin().size()).isEqualTo(1);
        assertThat(fooTaskAdminServer.taskAdminController.getAddressSet(appName))
            .isEqualTo(Set.of(executorContext.uri().toString()));
        processor.stop();

        counter = 5;
        while (processor.isRegistered() && counter-- > 0){
            Thread.sleep(Duration.ofMillis(100));
        }
        Set<String> addressSet = fooTaskAdminServer.taskAdminController.getAddressSet(appName);
        assertThat(addressSet.size()).isEqualTo(0);

    }

    @Test
    public void testExceptionalHandler() throws Exception{
        TaskExecutorContext executorContext = TaskExecutorContext.builder()
            .withAppName(appName)
            .withFqdn(fqdn)
            .withPort(port)
            .withListOfAdmin(List.of("foo-bar"))
            .withRootPath(rootPath.resolve("foo-bar"))
            .build();
        TaskExecutor taskExecutor = TaskExecutor.of(executorContext);
        RegistryProcessor processor = RegistryProcessor.builder()
            .withTaskExecutor(taskExecutor)
            .build();
        processor.start();

        int checkTimes = 5;
        while (!processor.isRegistered() && checkTimes-- > 0 ){
            assertThat(processor.isStarted()).isTrue();
            Thread.sleep(100);
        }
        processor.stop();
        assertThat(processor.isStarted()).isFalse();
        assertThat(processor.isRegistered()).isFalse();
    }

    @TempDir
    static Path rootPath;

    @BeforeAll
    public static void startTaskAdminServer() {
        fooTaskAdminServer = new FooTaskAdminServer();
        muServer = fooTaskAdminServer.getMuServer();
        System.out.println(muServer.uri());
        System.out.println(rootPath.toString());
    }

    @AfterAll
    public static void stopTaskAdminServer() {
        if (muServer != null) {
            muServer.stop();
        }
    }

    private static final String appName = "foo";
    private static final String fqdn = "localhost";
    private static final int port = NetUtil.findAvailablePort(12580);

    private static FooTaskAdminServer fooTaskAdminServer;
    private static MuServer muServer;


}
