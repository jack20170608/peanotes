package top.ilovemyhome.peanotes.common.task.exe;

import io.muserver.MuServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.exe.common.FooTaskAdminServer;
import top.ilovemyhome.peanotes.common.task.exe.domain.HandleCallbackParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.RegistryParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.TaskResponse;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.RegistType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TaskAdminTest {

    @Test
    public void testCallBack(){
        List<HandleCallbackParam> callbackParamList = List.of(
            new HandleCallbackParam(1L, "foo", 1L, LocalDateTime.now(), 200, null)
            , new HandleCallbackParam(2L, "bar", 2L, LocalDateTime.now(), 400, "Execution Failed")
        );
        TaskResponse response = taskAdmin.callback(callbackParamList);
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.msg()).isEqualTo("OK");
    }

    @Test
    public void testRegister(){
        RegistryParam registryParam = new RegistryParam(RegistType.EXECUTOR, "foo", "localhost:9090");
        TaskResponse response = taskAdmin.register(registryParam);
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.msg()).isEqualTo("OK");
    }

    @Test
    public void testUnRegister(){
        RegistryParam registryParam = new RegistryParam(RegistType.EXECUTOR, "foo", "localhost:9090");
        TaskResponse response = taskAdmin.unRegister(registryParam);
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.msg()).isEqualTo("OK");
    }

    @BeforeAll
    public static void init(){
        FooTaskAdminServer fooTaskAdminServer = new FooTaskAdminServer();
        muServer = fooTaskAdminServer.getMuServer();
        LOGGER.info("Server Uri=[{}].", muServer.httpUri());
        String adminBaseUrl = muServer.httpUri().toString();
        LOGGER.info("adminUrl is {}.", adminBaseUrl);
        taskAdmin = TaskAdmin.builder()
            .withAdminServerUrl(adminBaseUrl)
            .withAccessToken("foo-access-token")
            .build();
        assertThat(taskAdmin.getAdminServerurl()).isEqualTo(adminBaseUrl);
    }

    @AfterAll
    public static void destroy(){
        if (muServer != null) {
            muServer.stop();
        }
    }

    protected static TaskAdmin taskAdmin;
    protected static MuServer muServer;

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAdminTest.class);
}
