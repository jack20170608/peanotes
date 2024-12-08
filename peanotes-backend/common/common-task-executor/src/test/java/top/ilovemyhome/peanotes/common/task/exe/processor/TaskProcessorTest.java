package top.ilovemyhome.peanotes.common.task.exe.processor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.exe.*;
import top.ilovemyhome.peanotes.common.task.exe.domain.HandleCallbackParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.TriggerParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.ExecutorBlockStrategyEnum;
import top.ilovemyhome.peanotes.common.task.exe.tasks.SimpleTask;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static top.ilovemyhome.peanotes.common.task.exe.Constants.MAX_IDLE_POLL_TIMES;

public class TaskProcessorTest {

    @BeforeAll
    public static void initMock() {
        List<Object> taskBeans = new ArrayList<>();
        taskBeans.add(new SimpleTask());
        taskExecutorContext = TaskExecutorContext.builder()
            .withAppName("app2")
            .withFqdn("task1.jack007.top")
            .withListOfAdmin(List.of("admin1.jack007.top", "admin2.jack007.top"))
            .withRootPath(tempRootPath)
            .withHandlerBeans(taskBeans)
            .build();
        taskExecutor = Mockito.mock(TaskExecutor.class);
        taskCallbackProcessor = Mockito.mock(TaskCallbackProcessor.class);
        when(taskExecutor.getContext()).thenReturn(taskExecutorContext);
        when(taskExecutor.getCallbackProcessor()).thenReturn(taskCallbackProcessor);
    }

    @Test
    public void testThreadLocalCtx() throws Exception {
        Thread parentThread = new Thread(() -> {
            TaskContext t1Context = TaskContext.builder()
                .withTaskExecutor(taskExecutor)
                .withTaskId(100L)
                .withLogId(1001L)
                .withTaskName("HelloWorldTask")
                .withShardIndex(1)
                .withShardTotal(9)
                .build();
            TaskProcessor.CONTEXT.set(t1Context);
            //Create sub thread
            Thread subThread = new Thread(() -> {
                TaskContext subCtx = TaskProcessor.CONTEXT.get();
                assertThat(subCtx).isEqualTo(t1Context);
            });
            subThread.start();
        });
        parentThread.start();
        parentThread.join();
    }

    @Test
    public void testCreate() {
        TaskProcessor hiProcessor = createProcessor(100L, helloWorldHandler);
        assertThat(hiProcessor.isRunningOrHasQueue()).isFalse();
        assertThat(hiProcessor.getTaskHandler()).isEqualTo(helloWorldHandler);
    }

    @Test
    public void testPushTriggerQueue() throws Exception {
        var taskId = 100L;
        TaskProcessor hiProcessor = createProcessor(taskId, helloWorldHandler);
        LongStream.range(1, 100).boxed().forEach(logId -> {
            TriggerParam triggerParam = TriggerParam.builder()
                .withJobId(taskId)
                .withExecutorHandler("hiHandler")
                .withExecutorParams("foo")
                .withExecutorBlockStrategy(ExecutorBlockStrategyEnum.DISCARD_LATER)
                .withExecutorTimeout(Duration.ofSeconds(10))
                .withLogId(logId)
                .withLogDateTime(LocalDateTime.now())
                .withBroadcastIndex(1)
                .withBroadcastTotal(10)
                .build();
            hiProcessor.pushTriggerQueue(triggerParam);
        });
        hiProcessor.start();
        Thread.sleep(Duration.ofSeconds(1));
        assertThat(hiProcessor.isRunningOrHasQueue()).isFalse();
        hiProcessor.stop();
        Thread.sleep(Duration.ofSeconds(1));
        Mockito.verify(taskCallbackProcessor, times(99))
            .pushCallBack(any(HandleCallbackParam.class));
    }

    @Test
    public void testIdleTimeOverLimit() throws InterruptedException {
        var taskId = 100L;
        TaskProcessor hiProcessor = createProcessor(taskId, helloWorldHandler);
        hiProcessor.start();
        //The main thread wait for 15 seconds
        Thread.sleep(Duration.ofSeconds(Constants.POLL_INTERVAL * (MAX_IDLE_POLL_TIMES + 2)));
        Mockito.verify(taskCallbackProcessor, times(0)).pushCallBack(any());
        Mockito.verify(taskExecutor, times(1))
            .removeTaskThread(eq(taskId), any());
    }

    @Test
    public void testStopInTheMiddle() throws InterruptedException {
        var taskId = 100L;
        TaskProcessor taskProcessor = createProcessor(taskId, delay200msHandler);
        taskProcessor.start();
        LongStream.of(1, 2, 3).boxed().forEach(id -> {
            TriggerParam triggerParam = TriggerParam.builder()
                .withJobId(taskId)
                .withExecutorHandler("hiHandler")
                .withExecutorParams("foo")
                .withExecutorBlockStrategy(ExecutorBlockStrategyEnum.DISCARD_LATER)
                .withExecutorTimeout(Duration.ofSeconds(1))
                .withLogId(id)
                .withLogDateTime(LocalDateTime.now())
                .withBroadcastIndex(1)
                .withBroadcastTotal(10)
                .build();
            taskProcessor.pushTriggerQueue(triggerParam);
        });
        Thread.sleep(Duration.ofMillis(300L));
        taskProcessor.stop();
        Thread.sleep(Duration.ofMillis(1500L));


    }

    private TaskProcessor createProcessor(Long jobId, TaskHandler taskHandler) {
        return TaskProcessor.builder()
            .withTaskExecutor(taskExecutor)
            .withJobId(jobId)
            .withTaskHandler(taskHandler)
            .build();
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(TaskProcessorTest.class);


    @TempDir
    private static Path tempRootPath;

//    private static Path tempRootPath = Path.of("D:\\appvol\\data\\task\\executor");

    private static TaskExecutorContext taskExecutorContext;
    private static TaskExecutor taskExecutor;
    private static TaskCallbackProcessor taskCallbackProcessor;

    private static final TaskHandler helloWorldHandler = () -> LOGGER.info("Hello World");
    private static final TaskHandler exceptionalHandler = () -> {
        throw new RuntimeException("Some mocked exception happened.");
    };
    private static final TaskHandler SleepForEverHandler = () -> {
        try {
            LOGGER.info("Start....");
            Thread.sleep(Duration.ofDays(Integer.MAX_VALUE));
        } catch (InterruptedException e) {
            LOGGER.warn("Task interrupted.");
            throw new RuntimeException(e);
        }
    };


    private static final TaskHandler delay200msHandler = () -> {
        try {
            LOGGER.info("Slow handler invoked.");
            Thread.sleep(Duration.ofMillis(200));
            LOGGER.info("Slow handler finished.");
        }catch (InterruptedException ine){
            LOGGER.info("Interrupted");
        }
    };
}
