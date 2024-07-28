package top.ilovemyhome.peanotes.backend.common.task;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.ThreadUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.task.impl.*;
import top.ilovemyhome.peanotes.backend.common.task.impl.execution.ConditionExceptionalExecution;
import top.ilovemyhome.peanotes.backend.common.task.impl.execution.LongRunningExecution;
import top.ilovemyhome.peanotes.backend.common.task.impl.execution.PrintInputTaskExecution;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TaskDagServiceTest {

    @Test
    public void testTaskOrderAndRunForAsync(){
        TaskOrder fooDaily20240718 = SimpleTaskOrder.builder()
            .withName("foo")
            .withOrderType(OrderType.Daily)
            .withOtherKeys(Map.of("bizDate", "20240718", "runType", "ASYNC"))
            .build();
        String orderKey = "FOO_DAILY_20240718_ASYNC";
        assertThat(fooDaily20240718.getKey()).isEqualTo(orderKey);
        assertThat(taskDagService.isSuccess(fooDaily20240718)).isFalse();
        assertThat(taskDagService.isOrdered(fooDaily20240718)).isFalse();

        List<Task<String, String>> taskList =  taskDagService.order(fooDaily20240718);
        assertThat(taskList.size()).isEqualTo(5);
        assertThat(taskDagService.isSuccess(fooDaily20240718)).isFalse();
        assertThat(taskDagService.isOrdered(fooDaily20240718)).isTrue();
        Map<String, Task<String, String>> nameMap = Maps.newHashMap();

        taskList.forEach(t -> {
            nameMap.put(t.getName(), t);
            assertThat(t.getTaskStatus()).isEqualTo(TaskStatus.INIT);
            assertThat(t.getCreateDt()).isNotNull();
            assertThat(t.getInput().getTaskOrder()).isEqualTo(fooDaily20240718);
            assertThat(t.getOrderKey()).isEqualTo(orderKey);
            assertThat(t.getOutput()).isNull();
            assertThat(t.getLastUpdateDt()).isNotNull();
            if (t.getName().equals("t1")){
                assertThat(t.getPriorTasks()).isNull();
                assertThat(t.getSuccessorTasks().size()).isEqualTo(2);
                assertThat(t.getSuccessorTasks().stream().map(Task::getName).collect(Collectors.toSet())).isEqualTo(Set.of("t2", "t3"));
                assertThat(t.getSuccessorIds().size()).isEqualTo(2);
                assertThat(t.getInput().getInput()).isEqualTo("t1Input");
                assertThat(t.getTaskExecution()).isInstanceOf(PrintInputTaskExecution.class);
            }
            if (t.getName().equals("t2")){
                assertThat(t.getPriorTasks().size()).isEqualTo(1);
                assertThat(t.getPriorTasks().stream().map(Task::getName).collect(Collectors.toSet())).isEqualTo(Set.of("t1"));
                assertThat(t.getSuccessorTasks()).isNull();
                assertThat(t.getSuccessorIds()).isNull();
                assertThat(t.getInput().getInput()).isEqualTo("t2Input");
                assertThat(t.getTaskExecution()).isInstanceOf(PrintInputTaskExecution.class);
            }
            if (t.getName().equals("t3")){
                assertThat(t.getPriorTasks().size()).isEqualTo(1);
                assertThat(t.getPriorTasks().stream().map(Task::getName).collect(Collectors.toSet())).isEqualTo(Set.of("t1"));
                assertThat(t.getSuccessorTasks().size()).isEqualTo(2);
                assertThat(t.getSuccessorTasks().stream().map(Task::getName).collect(Collectors.toSet())).isEqualTo(Set.of("t5", "t4"));
                assertThat(t.getSuccessorIds().size()).isEqualTo(2);
                assertThat(t.getInput().getInput()).isEqualTo("t3Input");
                assertThat(t.getTaskExecution()).isInstanceOf(ConditionExceptionalExecution.class);
            }
            if (t.getName().equals("t4")){
                assertThat(t.getPriorTasks().size()).isEqualTo(1);
                assertThat(t.getPriorTasks().stream().map(Task::getName).collect(Collectors.toSet())).isEqualTo(Set.of("t3"));
                assertThat(t.getSuccessorTasks().size()).isEqualTo(1);
                assertThat(t.getSuccessorTasks().stream().map(Task::getName).collect(Collectors.toSet())).isEqualTo(Set.of("t5"));
                assertThat(t.getSuccessorIds().size()).isEqualTo(1);
                assertThat(t.getInput().getInput()).isEqualTo("t4Input");
                assertThat(t.getTaskExecution()).isInstanceOf(LongRunningExecution.class);
            }
            if (t.getName().equals("t5")){
                assertThat(t.getPriorTasks().size()).isEqualTo(2);
                assertThat(t.getPriorTasks().stream().map(Task::getName).collect(Collectors.toSet())).isEqualTo(Set.of("t3", "t4"));
                assertThat(t.getSuccessorTasks()).isNull();
                assertThat(t.getSuccessorIds()).isNull();
                assertThat(t.getInput().getInput()).isEqualTo("t5Input");
                assertThat(t.getTaskExecution()).isInstanceOf(PrintInputTaskExecution.class);
            }
        });
        Task<String, String> t1 = nameMap.get("t1");
        Task<String, String> t2 = nameMap.get("t2");
        Task<String, String> t3 = nameMap.get("t3");
        Task<String, String> t4 = nameMap.get("t4");
        Task<String, String> t5 = nameMap.get("t5");
        taskDagService.start(fooDaily20240718);
        ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
        taskDagService.receiveTaskEvent(nameMap.get("t1").getId(), TaskStatus.SUCCESS, StringTaskOutput.success("T1 success"));
        assertThat(t1.getTaskStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(t1.getOutput().getOutput()).isEqualTo("T1 success");
        assertThat(t1.getStartDt()).isNotNull();
        assertThat(t1.getEndDt()).isNotNull();
        ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
        assertThat(t2.getTaskStatus()).isEqualTo(TaskStatus.RUNNING);
        assertThat(t2.getStartDt()).isNotNull();
        assertThat(t3.getTaskStatus()).isEqualTo(TaskStatus.RUNNING);
        assertThat(t3.getStartDt()).isNotNull();
        assertThat(t4.getTaskStatus()).isEqualTo(TaskStatus.INIT);
        assertThat(t5.getTaskStatus()).isEqualTo(TaskStatus.INIT);


        taskDagService.receiveTaskEvent(nameMap.get("t2").getId(), TaskStatus.SUCCESS, StringTaskOutput.success("T2 success"));
        ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
        assertThat(t2.getTaskStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(t3.getTaskStatus()).isEqualTo(TaskStatus.RUNNING);
        assertThat(t4.getTaskStatus()).isEqualTo(TaskStatus.INIT);
        assertThat(t5.getTaskStatus()).isEqualTo(TaskStatus.INIT);

        taskDagService.receiveTaskEvent(nameMap.get("t3").getId(), TaskStatus.SUCCESS, StringTaskOutput.success("T3 success"));
        ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
        assertThat(t1.getTaskStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(t2.getTaskStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(t3.getTaskStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(t4.getTaskStatus()).isEqualTo(TaskStatus.RUNNING);
        assertThat(t5.getTaskStatus()).isEqualTo(TaskStatus.INIT);

        ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
        assertThat(t4.getTaskStatus()).isEqualTo(TaskStatus.RUNNING);
        ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
        taskDagService.receiveTaskEvent(nameMap.get("t4").getId(), TaskStatus.SUCCESS, StringTaskOutput.success("T4 success"));
        assertThat(t4.getTaskStatus()).isEqualTo(TaskStatus.SUCCESS);
        ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
        assertThat(t5.getTaskStatus()).isEqualTo(TaskStatus.RUNNING);

        taskDagService.receiveTaskEvent(nameMap.get("t5").getId(), TaskStatus.SUCCESS, StringTaskOutput.success("T5 success"));
        assertThat(t5.getTaskStatus()).isEqualTo(TaskStatus.SUCCESS);

        assertThat(taskDagService.isSuccess(fooDaily20240718)).isTrue();

    }


    @Test
    public void testTaskOrderAndRunForSync(){
        TaskOrder fooDaily20240718 = SimpleTaskOrder.builder()
            .withName("foo")
            .withOrderType(OrderType.Daily)
            .withOtherKeys(Map.of("bizDate", "20240718", "runType", "SYNC"))
            .build();
        String orderKey = "FOO_DAILY_20240718_SYNC";
        assertThat(fooDaily20240718.getKey()).isEqualTo(orderKey);
        assertThat(taskDagService.isSuccess(fooDaily20240718)).isFalse();
        assertThat(taskDagService.isOrdered(fooDaily20240718)).isFalse();

        List<Task<String, String>> taskList =  taskDagService.order(fooDaily20240718);
        assertThat(taskList.size()).isEqualTo(5);
        assertThat(taskDagService.isSuccess(fooDaily20240718)).isFalse();
        assertThat(taskDagService.isOrdered(fooDaily20240718)).isTrue();
        Map<String, Task<String, String>> nameMap = Maps.newHashMap();

        taskList.forEach(t -> {
            nameMap.put(t.getName(), t);
            assertThat(t.getTaskStatus()).isEqualTo(TaskStatus.INIT);
            assertThat(t.getCreateDt()).isNotNull();
            assertThat(t.getInput().getTaskOrder()).isEqualTo(fooDaily20240718);
            assertThat(t.getOrderKey()).isEqualTo(orderKey);
            assertThat(t.getOutput()).isNull();
            assertThat(t.getLastUpdateDt()).isNotNull();
            if (t.getName().equals("t1")){
                assertThat(t.getPriorTasks()).isNull();
                assertThat(t.getSuccessorTasks().size()).isEqualTo(2);
                assertThat(t.getSuccessorTasks().stream().map(Task::getName).collect(Collectors.toSet())).isEqualTo(Set.of("t2", "t3"));
                assertThat(t.getSuccessorIds().size()).isEqualTo(2);
                assertThat(t.getInput().getInput()).isEqualTo("t1Input");
                assertThat(t.getTaskExecution()).isInstanceOf(PrintInputTaskExecution.class);
            }
            if (t.getName().equals("t2")){
                assertThat(t.getPriorTasks().size()).isEqualTo(1);
                assertThat(t.getPriorTasks().stream().map(Task::getName).collect(Collectors.toSet())).isEqualTo(Set.of("t1"));
                assertThat(t.getSuccessorTasks()).isNull();
                assertThat(t.getSuccessorIds()).isNull();
                assertThat(t.getInput().getInput()).isEqualTo("t2Input");
                assertThat(t.getTaskExecution()).isInstanceOf(PrintInputTaskExecution.class);
            }
            if (t.getName().equals("t3")){
                assertThat(t.getPriorTasks().size()).isEqualTo(1);
                assertThat(t.getPriorTasks().stream().map(Task::getName).collect(Collectors.toSet())).isEqualTo(Set.of("t1"));
                assertThat(t.getSuccessorTasks().size()).isEqualTo(2);
                assertThat(t.getSuccessorTasks().stream().map(Task::getName).collect(Collectors.toSet())).isEqualTo(Set.of("t5", "t4"));
                assertThat(t.getSuccessorIds().size()).isEqualTo(2);
                assertThat(t.getInput().getInput()).isEqualTo("t3Input");
                assertThat(t.getTaskExecution()).isInstanceOf(ConditionExceptionalExecution.class);
            }
            if (t.getName().equals("t4")){
                assertThat(t.getPriorTasks().size()).isEqualTo(1);
                assertThat(t.getPriorTasks().stream().map(Task::getName).collect(Collectors.toSet())).isEqualTo(Set.of("t3"));
                assertThat(t.getSuccessorTasks().size()).isEqualTo(1);
                assertThat(t.getSuccessorTasks().stream().map(Task::getName).collect(Collectors.toSet())).isEqualTo(Set.of("t5"));
                assertThat(t.getSuccessorIds().size()).isEqualTo(1);
                assertThat(t.getInput().getInput()).isEqualTo("t4Input");
                assertThat(t.getTaskExecution()).isInstanceOf(LongRunningExecution.class);
            }
            if (t.getName().equals("t5")){
                assertThat(t.getPriorTasks().size()).isEqualTo(2);
                assertThat(t.getPriorTasks().stream().map(Task::getName).collect(Collectors.toSet())).isEqualTo(Set.of("t3", "t4"));
                assertThat(t.getSuccessorTasks()).isNull();
                assertThat(t.getSuccessorIds()).isNull();
                assertThat(t.getInput().getInput()).isEqualTo("t5Input");
                assertThat(t.getTaskExecution()).isInstanceOf(PrintInputTaskExecution.class);
            }
        });
        Task<String, String> t1 = nameMap.get("t1");
        Task<String, String> t2 = nameMap.get("t2");
        Task<String, String> t3 = nameMap.get("t3");
        Task<String, String> t4 = nameMap.get("t4");
        Task<String, String> t5 = nameMap.get("t5");
        taskDagService.start(fooDaily20240718);

        ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
        assertThat(t1.getTaskStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(t1.getOutput().getOutput()).isEqualTo("t1Input->PrintInputTaskExecution");
        assertThat(t1.getStartDt()).isNotNull();
        assertThat(t1.getEndDt()).isNotNull();

        assertThat(t2.getTaskStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(t2.getOutput().getOutput()).isEqualTo("t2Input->PrintInputTaskExecution");
        assertThat(t2.getStartDt()).isNotNull();
        assertThat(t2.getEndDt()).isNotNull();

        assertThat(t3.getTaskStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(t3.getOutput().getOutput()).isEqualTo("t3Input->ConditionExceptionalExecution");
        assertThat(t3.getInput().getInput()).isEqualTo("t3Input");
        assertThat(t3.getStartDt()).isNotNull();
        assertThat(t3.getEndDt()).isNotNull();

        assertThat(t4.getTaskStatus()).isEqualTo(TaskStatus.RUNNING);
        assertThat(t4.getStartDt()).isNotNull();
        assertThat(t5.getTaskStatus()).isEqualTo(TaskStatus.INIT);
        assertThat(t5.getStartDt()).isNull();

        ThreadUtils.sleepQuietly(Duration.ofSeconds(3));
        assertThat(t4.getTaskStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(t4.getEndDt()).isNotNull();
        assertThat(t4.getOutput().getOutput()).isEqualTo("t4Input->LongRunningExecution");
        assertThat(t5.getTaskStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(t5.getEndDt()).isNotNull();
        assertThat(t5.getOutput().getOutput()).isEqualTo("t5Input->PrintInputTaskExecution");

        assertThat(taskDagService.isSuccess(fooDaily20240718)).isTrue();

    }


    @BeforeAll
    public static void init(){
        int nThreads = Runtime.getRuntime().availableProcessors();
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("TaskDagService-%d").build();
        pool = new ThreadPoolExecutor(nThreads, 16, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024)
            , namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        taskDao = new FooTaskDaoImpl();
        taskContext = new TaskContext<>(taskDao, pool);
        fooTaskFactory = new FooTaskFactoryImpl(taskContext);
        taskContext.setTaskFactory(fooTaskFactory);
        taskDagService = new TaskDagServiceImpl<>(taskContext);

    }

    private static TaskDao<String, String> taskDao;

    private static ExecutorService pool;
    private static TaskContext<String, String> taskContext;
    private static TaskDagService<String, String> taskDagService;
    private static FooTaskFactoryImpl fooTaskFactory;

    private static final AtomicLong ID_SEQ = new AtomicLong();
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskDagServiceTest.class);

}
