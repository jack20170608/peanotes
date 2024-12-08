package top.ilovemyhome.peanotes.common.task.exe;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.exe.common.FooTaskAdminServer;
import top.ilovemyhome.peanotes.common.task.exe.processor.RegistryProcessor;
import top.ilovemyhome.peanotes.common.task.exe.processor.TaskCallbackProcessor;
import top.ilovemyhome.peanotes.common.task.exe.tasks.SimpleTask;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class TaskExecutorTest {

    private static TaskExecutor taskExecutor;



    @BeforeAll
    public static void initMock() {
        initTheTaskAdmin();
        taskExecutorContext = TaskExecutorContext.builder()
            .withSchema("Http")
            .withAppName("foo")
            .withFqdn("localhost")
            .withPort(0)
            .withListOfAdmin(List.of(adminServer.getMuServer().httpUri().toString()))
            .withRootPath(tempRootPath)
            .withHandlerBeans(initHandlerBeans())
            .build();
        taskExecutor= TaskExecutor.of(taskExecutorContext);

        assertThat(taskExecutor).isNotNull();
        assertThat(taskExecutor.getTaskAdmins()).isNotNull();
        assertThat(taskExecutor.getContext()).isEqualTo(taskExecutorContext);
        assertThat(taskExecutor.getRegistryProcessor()).isNotNull();
        assertThat(taskExecutor.getCallbackProcessor()).isNotNull();
        RegistryProcessor registryProcessor = taskExecutor.getRegistryProcessor();
        assertThat(registryProcessor.getState()).isEqualTo(LifeCycle.State.INITIALIZED);
        TaskCallbackProcessor callbackProcessor = taskExecutor.getCallbackProcessor();
        assertThat(callbackProcessor.getState()).isEqualTo(LifeCycle.State.INITIALIZED);
    }

    @AfterAll
    public static void destroy(){
        if (adminServer != null) {
            adminServer.stop();
        }
    }


    @Test
    public void testExecutorStartStop() throws InterruptedException {
        assertThat(taskExecutor.getState()).isEqualTo(LifeCycle.State.INITIALIZED);
        taskExecutor.start();
        RegistryProcessor registryProcessor = taskExecutor.getRegistryProcessor();
        TaskCallbackProcessor callbackProcessor = taskExecutor.getCallbackProcessor();
        assertThat(registryProcessor.getState()).isEqualTo(LifeCycle.State.STARTED);
        assertThat(callbackProcessor.getState()).isEqualTo(LifeCycle.State.STARTED);

        //Just wait until the first beat
        while (!taskExecutor.isStarted()){
            LOGGER.info("Wait for the executor started!");
            Thread.sleep(50);
        }
        if (taskExecutor.isRegistered()) {
            //Wait the 1st cycle register to the admin status
            Thread.sleep(500);
            Set<String> registerDb = adminServer.getTaskAdminController().getAddressSet("foo");
            assertThat(registerDb.size()).isEqualTo(1);
            URI uri = taskExecutorContext.uri();
            assertThat(registerDb.contains(uri.toString()));
        }

        taskExecutor.stop();
        while (!taskExecutor.isStopped()){
            LOGGER.info("Wait for the executor stopped!");
            Thread.sleep(50);
        }
        assertThat(registryProcessor.getState()).isEqualTo(LifeCycle.State.STOPPED);
        assertThat(callbackProcessor.getState()).isEqualTo(LifeCycle.State.STOPPED);
        if (taskExecutor.isStopped()){
            Thread.sleep(500);
            Set<String> registerDb = adminServer.getTaskAdminController().getAddressSet("foo");
            assertThat(registerDb.size()).isEqualTo(0);
        }
    }

    @Test
    public void testExecutorInitFailureWithNonAccessableAdminServer() throws InterruptedException {
        TaskExecutorContext emptyAdminAddress = TaskExecutorContext.builder()
            .withAppName("foo")
            .withFqdn("localhost")
            .withPort(0)
            .withListOfAdmin(List.of("foo"))
            .withRootPath(tempRootPath)
            .build();
        TaskExecutor badExecutor = TaskExecutor.of(emptyAdminAddress);
        badExecutor.start();

        //Just wait until the first beat
        while (!badExecutor.isStarted()){
            LOGGER.info("Wait for the executor started!");
            Thread.sleep(50);
        }
        if (badExecutor.isStarted()) {
            //Wait the 1st cycle register to the admin status
            Thread.sleep(500);
        }

    }

    private static List<Object> initHandlerBeans(){
        SimpleTask simpleTask = new SimpleTask();
        return List.of(simpleTask);
    }

    @TempDir
    private static Path tempRootPath ;

    private static TaskExecutorContext taskExecutorContext;

    private static void initTheTaskAdmin(){
        adminServer = new FooTaskAdminServer();
        LOGGER.info("Http admin uri is {}.", adminServer.getMuServer().httpUri());
    }


    protected static FooTaskAdminServer adminServer;

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutorTest.class);
}
