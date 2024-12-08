package top.ilovemyhome.peanotes.common.task.exe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import top.ilovemyhome.peanotes.common.task.exe.handler.MethodTaskHandler;
import top.ilovemyhome.peanotes.common.task.exe.tasks.SimpleTask;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class TaskExecutorContextTest {

    @TempDir
    static Path tempRootPath;

    @Test
    public void testCreateTaskExecutorContextWithAllDefault() {
        TaskExecutorContext context1 = TaskExecutorContext.builder()
            .withHandlerBeans(List.of(new SimpleTask()))
            .withFqdn("localhost")
            .withPort(12580)
            .withListOfAdmin(List.of("admin1.jack007.top", "admin2.jack007.top"))
            .build();
        assertThat(context1.getAppName()).isEqualTo(TaskExecutorContext.DEFAULT_APP_NAME);
        assertThat(context1.uri().toString()).isEqualTo("https://localhost:12580/task");
        assertThat(context1.getListOfAdmin()).isEqualTo(List.of("admin1.jack007.top", "admin2.jack007.top"));
        Path defaultRootPath = TaskExecutorContext.DEFAULT_ROOT_PATH;
        assertThat(context1.getLogRootPath()).isEqualTo(defaultRootPath.resolve("logs"));
        assertThat(context1.getScriptSourcePath()).isEqualTo(defaultRootPath.resolve("scripts"));
        assertThat(context1.getFailCallbackFilePath()).isEqualTo(defaultRootPath.resolve("failcallback"));
        assertThat(context1.getTaskHandler("simpleHandler")).isInstanceOf(MethodTaskHandler.class);
        assertThat(context1.getTaskHandler("lifeCycleHandler")).isInstanceOf(MethodTaskHandler.class);
    }

    @Test
    public void testCreateTaskExecutorContext() {
        Path rootPath = tempRootPath.resolve("t1");
        TaskExecutorContext context1 = TaskExecutorContext.builder()
            .withAppName("app1")
            .withSchema("HTTP")
            .withFqdn("task1.jack007.top")
            .withHandlerBeans(List.of(new SimpleTask()))
            .withPort(10000)
            .withPath("/some-path/task")
            .withListOfAdmin(List.of("admin1.jack007.top", "admin2.jack007.top"))
            .withRootPath(rootPath)
            .build();
        assertThat(context1.getAppName()).isEqualTo("app1");
        assertThat(context1.uri().toString()).isEqualTo("http://task1.jack007.top:10000/some-path/task");
        assertThat(context1.getListOfAdmin()).isEqualTo(List.of("admin1.jack007.top", "admin2.jack007.top"));
        assertThat(context1.getLogRootPath()).isEqualTo(rootPath.resolve("logs"));
        assertThat(context1.getScriptSourcePath()).isEqualTo(rootPath.resolve("scripts"));
        assertThat(context1.getFailCallbackFilePath()).isEqualTo(rootPath.resolve("failcallback"));
        assertThat(context1.getTaskHandler("simpleHandler")).isInstanceOf(MethodTaskHandler.class);
        assertThat(context1.getTaskHandler("lifeCycleHandler")).isInstanceOf(MethodTaskHandler.class);
    }

    @Test
    public void testCreateTaskExecutorContextWithWholeCostomize() {
        Path rootPath = tempRootPath.resolve("t2");
        TaskExecutorContext context2 = TaskExecutorContext.builder()
            .withAppName("app2")
            .withFqdn("task1.jack007.top")
            .withHandlerBeans(List.of(new SimpleTask()))
            .withPort(10000)
            .withListOfAdmin(List.of("admin1.jack007.top", "admin2.jack007.top"))
            .withRootPath(rootPath)
            .withLogRootPath(rootPath.resolve("logs/12345"))
            .withScriptSourcePath(rootPath.resolve("bin"))
            .withFailCallbackFilePath(rootPath.resolve("callback/20241128"))
            .build();
        assertThat(context2.getAppName()).isEqualTo("app2");
        assertThat(context2.uri().toString()).isEqualTo("https://task1.jack007.top:10000/task");
        assertThat(context2.getListOfAdmin()).isEqualTo(List.of("admin1.jack007.top", "admin2.jack007.top"));
        assertThat(context2.getFailCallbackFilePath()).isEqualTo(rootPath.resolve("callback/20241128"));
        assertThat(context2.getTaskHandler("simpleHandler")).isInstanceOf(MethodTaskHandler.class);
        assertThat(context2.getTaskHandler("lifeCycleHandler")).isInstanceOf(MethodTaskHandler.class);

        Path logRootPath = context2.getLogRootPath();
        assertThat(logRootPath).isEqualTo(rootPath.resolve("logs/12345"));
        assertThat(Files.isDirectory(logRootPath)).isTrue();
        assertThat(Files.exists(logRootPath)).isTrue();

        Path scriptSourcePath = context2.getScriptSourcePath();
        assertThat(scriptSourcePath).isEqualTo(rootPath.resolve("bin"));
        assertThat(Files.isDirectory(scriptSourcePath)).isTrue();
        assertThat(Files.exists(scriptSourcePath)).isTrue();

        Path callbackPath = context2.getFailCallbackFilePath();
        assertThat(callbackPath).isEqualTo(rootPath.resolve("callback/20241128"));
        assertThat(Files.isDirectory(callbackPath)).isTrue();
        assertThat(Files.exists(callbackPath)).isTrue();
    }
}
