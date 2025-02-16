package top.ilovemyhome.peanotes.backend.common.task;

import org.apache.commons.lang3.ThreadUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.db.SimpleDataSourceFactory;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.common.task.impl.*;
import top.ilovemyhome.peanotes.backend.common.task.persistent.TaskOrderDaoJdbiImpl;
import top.ilovemyhome.peanotes.backend.common.task.persistent.TaskOrder;
import top.ilovemyhome.peanotes.backend.common.task.persistent.TaskRecord;
import top.ilovemyhome.peanotes.backend.common.task.persistent.TaskRecordDaoJdbiImpl;
import top.ilovemyhome.peanotes.backend.common.task.utils.TestUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled
public class TaskDagServiceTest {

    @Test
    public void testTaskOrderAndRunForAsync() {
        final String orderKey = "FOO_DAILY_20240718_ASYNC";
        TaskOrder fooDaily20240718 = TaskOrder.builder()
            .withId(1L)
            .withName("foo")
            .withOrderType(OrderType.Daily)
            .withKey(orderKey)
            .withAttributes(Map.of("type", "fund"))
            .build();
        taskDagService.deleteOrderByKey(orderKey, true);
        taskDagService.createOrder(fooDaily20240718);

        assertThat(fooDaily20240718.getKey()).isEqualTo(orderKey);
        assertThat(taskDagService.isSuccess(orderKey)).isFalse();
        assertThat(taskDagService.isOrdered(fooDaily20240718.getKey())).isTrue();
        List<Long> ids = taskDagService.getNextTaskIds(5);
        //add the tasks
        TaskRecord t1 = createT1(ids.getFirst(), fooDaily20240718, true, "t1Input", Set.of(ids.get(1), ids.get(2)));
        TaskRecord t2 = createT2(ids.get(1), fooDaily20240718, true, "t2Input", null);
        TaskRecord t3 = createT3(ids.get(2), fooDaily20240718, true, "t3Input", Set.of(ids.get(3), ids.get(4)));
        TaskRecord t4 = createT4(ids.get(3), fooDaily20240718, true, "t4Input", Set.of(ids.get(4)), 2L);
        TaskRecord t5 = createT5(ids.get(4), fooDaily20240718, true, "t5Input", null);
        taskDagService.createTasks(List.of(t1, t2, t3, t4, t5));
        List<TaskRecord> taskList = taskDagService.findTaskByOrderKey(orderKey);
        assertThat(taskList.size()).isEqualTo(5);

        assertThat(taskDagService.isSuccess(orderKey)).isFalse();
        assertThat(taskDagService.isOrdered(fooDaily20240718.getKey())).isTrue();
        Map<String, TaskRecord> nameMap = queryAndReturnMap(orderKey);

        taskList.forEach(t -> {
            assertThat(t.getStatus()).isEqualTo(TaskStatus.INIT);
            assertThat(t.getCreateDt()).isNotNull();
            assertThat(t.getInput()).isNotNull();
            assertThat(t.getOrderKey()).isEqualTo(orderKey);
            assertThat(t.getOutput()).isNull();
            assertThat(t.getLastUpdateDt()).isNotNull();
        });

        t1 = nameMap.get("t1");
        t2 = nameMap.get("t2");
        t3 = nameMap.get("t3");
        t4 = nameMap.get("t4");
        t5 = nameMap.get("t5");

        assertThat(t1.getSuccessorIds()).isEqualTo(Set.of(ids.get(1), ids.get(2)));
        assertThat(JacksonUtil.fromJson(t1.getInput(), StringTaskInput.class).getTaskId()).isEqualTo(t1.getId());
        assertThat(t1.getExecutionKey()).isEqualTo("top.ilovemyhome.peanotes.backend.common.task.impl.execution.PrintInputTaskExecution");
        assertThat(t1.getTimeout()).isEqualTo(1L);
        assertThat(t1.getTimeoutUnit()).isEqualTo(TimeUnit.HOURS);

        assertThat(t2.getSuccessorIds()).isNull();
        assertThat(JacksonUtil.fromJson(t2.getInput(), StringTaskInput.class).getTaskId()).isEqualTo(t2.getId());
        assertThat(t2.getExecutionKey()).isEqualTo("top.ilovemyhome.peanotes.backend.common.task.impl.execution.PrintInputTaskExecution");
        assertThat(t2.getTimeout()).isEqualTo(1L);
        assertThat(t2.getTimeoutUnit()).isEqualTo(TimeUnit.MINUTES);

        assertThat(t3.getSuccessorIds()).isEqualTo(Set.of(ids.get(3), ids.get(4)));
        assertThat(JacksonUtil.fromJson(t3.getInput(), StringTaskInput.class).getTaskId()).isEqualTo(t3.getId());
        assertThat(t3.getExecutionKey()).isEqualTo("top.ilovemyhome.peanotes.backend.common.task.impl.execution.ConditionExceptionalExecution");
        assertThat(t3.getTimeout()).isEqualTo(10L);
        assertThat(t3.getTimeoutUnit()).isEqualTo(TimeUnit.SECONDS);

        assertThat(t4.getSuccessorIds()).isEqualTo(Set.of(ids.get(4)));
        assertThat(JacksonUtil.fromJson(t4.getInput(), StringTaskInput.class).getTaskId()).isEqualTo(t4.getId());
        assertThat(t4.getExecutionKey()).isEqualTo("top.ilovemyhome.peanotes.backend.common.task.impl.execution.LongRunningExecution");
        assertThat(t4.getTimeout()).isEqualTo(2L);
        assertThat(t4.getTimeoutUnit()).isEqualTo(TimeUnit.SECONDS);

        assertThat(t5.getSuccessorIds()).isNull();
        assertThat(JacksonUtil.fromJson(t5.getInput(), StringTaskInput.class).getTaskId()).isEqualTo(t5.getId());
        assertThat(t5.getExecutionKey()).isEqualTo("top.ilovemyhome.peanotes.backend.common.task.impl.execution.PrintInputTaskExecution");
        assertThat(t5.getTimeout()).isEqualTo(10L);
        assertThat(t5.getTimeoutUnit()).isEqualTo(TimeUnit.SECONDS);

        taskDagService.loadAndStart(orderKey);
        ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
        taskDagService.receiveTaskEvent(t1.getId(), TaskStatus.SUCCESS, StringTaskOutput.success("T1 success"));
        nameMap = queryAndReturnMap(orderKey);
        assertThat(nameMap.get("t1").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t1").getOutput()).isEqualTo("T1 success");
        assertThat(nameMap.get("t1").getStartDt()).isNotNull();
        assertThat(nameMap.get("t1").getEndDt()).isNotNull();
        ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
        nameMap = queryAndReturnMap(orderKey);
        assertThat(nameMap.get("t2").getStatus()).isEqualTo(TaskStatus.RUNNING);
        assertThat(nameMap.get("t2").getStartDt()).isNotNull();
        assertThat(nameMap.get("t3").getStatus()).isEqualTo(TaskStatus.RUNNING);
        assertThat(nameMap.get("t3").getStartDt()).isNotNull();
        assertThat(nameMap.get("t4").getStatus()).isEqualTo(TaskStatus.INIT);
        assertThat(nameMap.get("t5").getStatus()).isEqualTo(TaskStatus.INIT);

        taskDagService.receiveTaskEvent(t2.getId(), TaskStatus.SUCCESS, StringTaskOutput.success("T2 success"));
        ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
        nameMap = queryAndReturnMap(orderKey);
        assertThat(nameMap.get("t2").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t3").getStatus()).isEqualTo(TaskStatus.RUNNING);
        assertThat(nameMap.get("t4").getStatus()).isEqualTo(TaskStatus.INIT);
        assertThat(nameMap.get("t5").getStatus()).isEqualTo(TaskStatus.INIT);

        taskDagService.receiveTaskEvent(t3.getId(), TaskStatus.SUCCESS, StringTaskOutput.success("T3 success"));
        ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
        nameMap = queryAndReturnMap(orderKey);
        assertThat(nameMap.get("t3").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t4").getStatus()).isEqualTo(TaskStatus.RUNNING);
        assertThat(nameMap.get("t5").getStatus()).isEqualTo(TaskStatus.INIT);

        ThreadUtils.sleepQuietly(Duration.ofSeconds(2));
        nameMap = queryAndReturnMap(orderKey);
        assertThat(nameMap.get("t4").getStatus()).isEqualTo(TaskStatus.RUNNING);
        assertThat(nameMap.get("t5").getStatus()).isEqualTo(TaskStatus.INIT);

        taskDagService.receiveTaskEvent(t4.getId(), TaskStatus.SUCCESS, StringTaskOutput.success("T4 success"));
        ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
        nameMap = queryAndReturnMap(orderKey);
        assertThat(nameMap.get("t4").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t5").getStatus()).isEqualTo(TaskStatus.RUNNING);

        taskDagService.receiveTaskEvent(t5.getId(), TaskStatus.SUCCESS, StringTaskOutput.success("T5 success"));
        nameMap = queryAndReturnMap(orderKey);
        assertThat(nameMap.get("t5").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(taskDagService.isSuccess(orderKey)).isTrue();
    }


    @Test
    public void testTaskOrderAndRunForSync() {
        String orderKey = "FOO_DAILY_20240718_SYNC";
        TaskOrder fooDaily20240718 = TaskOrder.builder()
            .withName("foo")
            .withKey(orderKey)
            .withOrderType(OrderType.Daily)
            .withAttributes(Map.of("type", "fund"))
            .build();
        taskDagService.deleteOrderByKey(orderKey, true);
        taskDagService.createOrder(fooDaily20240718);

        List<Long> ids = taskDagService.getNextTaskIds(5);
        TaskRecord t1 = createT1(ids.getFirst(), fooDaily20240718, false, "t1Input", Set.of(ids.get(1), ids.get(2)));
        TaskRecord t2 = createT2(ids.get(1), fooDaily20240718, false, "t2Input", null);
        TaskRecord t3 = createT3(ids.get(2), fooDaily20240718, false, "t3Input", Set.of(ids.get(3), ids.get(4)));
        TaskRecord t4 = createT4(ids.get(3), fooDaily20240718, false, "t4Input", Set.of(ids.get(4)), 10L);
        TaskRecord t5 = createT5(ids.get(4), fooDaily20240718, false, "t5Input", null);
        taskDagService.createTasks(List.of(t1, t2, t3, t4, t5));

        assertThat(fooDaily20240718.getKey()).isEqualTo(orderKey);
        assertThat(taskDagService.isSuccess(orderKey)).isFalse();
        assertThat(taskDagService.isOrdered(fooDaily20240718.getKey())).isTrue();
        Map<String, TaskRecord> nameMap;
        List<TaskRecord> taskList = taskDagService.findTaskByOrderKey(orderKey);
        assertThat(taskDagService.isSuccess(orderKey)).isFalse();
        assertThat(taskList.size()).isEqualTo(5);
        nameMap = queryAndReturnMap(orderKey);
        taskList.forEach(t -> {
            assertThat(t.isAsync()).isFalse();
            assertThat(t.getStatus()).isEqualTo(TaskStatus.INIT);
            assertThat(t.getCreateDt()).isNotNull();
            assertThat(t.getInput()).isNotNull();
            assertThat(t.getOrderKey()).isEqualTo(orderKey);
            assertThat(t.getOutput()).isNull();
            assertThat(t.getLastUpdateDt()).isNotNull();
            assertThat(t.isSuccess()).isFalse();
        });

        t1 = nameMap.get("t1");
        t2 = nameMap.get("t2");
        t3 = nameMap.get("t3");
        t4 = nameMap.get("t4");
        t5 = nameMap.get("t5");

        assertThat(t1.getSuccessorIds()).isEqualTo(Set.of(ids.get(1), ids.get(2)));
        assertThat(JacksonUtil.fromJson(t1.getInput(), StringTaskInput.class).getTaskId()).isEqualTo(t1.getId());
        assertThat(t1.getExecutionKey()).isEqualTo("top.ilovemyhome.peanotes.backend.common.task.impl.execution.PrintInputTaskExecution");
        assertThat(t1.getTimeout()).isEqualTo(1L);
        assertThat(t1.getTimeoutUnit()).isEqualTo(TimeUnit.HOURS);

        assertThat(t2.getSuccessorIds()).isNull();
        assertThat(JacksonUtil.fromJson(t2.getInput(), StringTaskInput.class).getTaskId()).isEqualTo(t2.getId());
        assertThat(t2.getExecutionKey()).isEqualTo("top.ilovemyhome.peanotes.backend.common.task.impl.execution.PrintInputTaskExecution");
        assertThat(t2.getTimeout()).isEqualTo(1L);
        assertThat(t2.getTimeoutUnit()).isEqualTo(TimeUnit.MINUTES);

        assertThat(t3.getSuccessorIds()).isEqualTo(Set.of(ids.get(3), ids.get(4)));
        assertThat(JacksonUtil.fromJson(t3.getInput(), StringTaskInput.class).getTaskId()).isEqualTo(t3.getId());
        assertThat(t3.getExecutionKey()).isEqualTo("top.ilovemyhome.peanotes.backend.common.task.impl.execution.ConditionExceptionalExecution");
        assertThat(t3.getTimeout()).isEqualTo(10L);
        assertThat(t3.getTimeoutUnit()).isEqualTo(TimeUnit.SECONDS);

        assertThat(t4.getSuccessorIds()).isEqualTo(Set.of(ids.get(4)));
        assertThat(JacksonUtil.fromJson(t4.getInput(), StringTaskInput.class).getTaskId()).isEqualTo(t4.getId());
        assertThat(t4.getExecutionKey()).isEqualTo("top.ilovemyhome.peanotes.backend.common.task.impl.execution.LongRunningExecution");
        assertThat(t4.getTimeout()).isEqualTo(10L);
        assertThat(t4.getTimeoutUnit()).isEqualTo(TimeUnit.SECONDS);

        assertThat(t5.getSuccessorIds()).isNull();
        assertThat(JacksonUtil.fromJson(t5.getInput(), StringTaskInput.class).getTaskId()).isEqualTo(t5.getId());
        assertThat(t5.getExecutionKey()).isEqualTo("top.ilovemyhome.peanotes.backend.common.task.impl.execution.PrintInputTaskExecution");
        assertThat(t5.getTimeout()).isEqualTo(10L);
        assertThat(t5.getTimeoutUnit()).isEqualTo(TimeUnit.SECONDS);

        taskDagService.loadAndStart(orderKey);
        ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
        nameMap = queryAndReturnMap(orderKey);
        assertThat(nameMap.get("t1").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t2").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t3").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t4").getStatus()).isEqualTo(TaskStatus.RUNNING);
        assertThat(nameMap.get("t5").getStatus()).isEqualTo(TaskStatus.INIT);

        ThreadUtils.sleepQuietly(Duration.ofSeconds(2));
        nameMap = queryAndReturnMap(orderKey);
        assertThat(nameMap.get("t1").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t2").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t3").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t4").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t5").getStatus()).isEqualTo(TaskStatus.SUCCESS);


    }


    @Test
    public void testTaskOrderAndRunForSyncTimeout() {
        String orderKey = "FOO_DAILY_20240718_TIMEOUT_SYNC";
        TaskOrder fooDaily20240718 = TaskOrder.builder()
            .withName("foo")
            .withOrderType(OrderType.Daily)
            .withKey(orderKey)
            .withAttributes(Map.of("bizDate", "20240718", "runType", "SYNC", "error", "TIMEOUT"))
            .build();
        taskDagService.deleteOrderByKey(orderKey, true);
        taskDagService.createOrder(fooDaily20240718);

        List<Long> ids = taskDagService.getNextTaskIds(5);
        TaskRecord t1 = createT1(ids.getFirst(), fooDaily20240718, false, "t1Input", Set.of(ids.get(1), ids.get(2)));
        TaskRecord t2 = createT2(ids.get(1), fooDaily20240718, false, "t2Input", null);
        TaskRecord t3 = createT3(ids.get(2), fooDaily20240718, false, "t3Input", Set.of(ids.get(3), ids.get(4)));
        TaskRecord t4 = createT4(ids.get(3), fooDaily20240718, false, "t4Input", Set.of(ids.get(4)), 1L);
        TaskRecord t5 = createT5(ids.get(4), fooDaily20240718, false, "t5Input", null);
        taskDagService.createTasks(List.of(t1, t2, t3, t4, t5));

        assertThat(fooDaily20240718.getKey()).isEqualTo(orderKey);
        Map<String, TaskRecord> nameMap = queryAndReturnMap(orderKey);
        t4 = nameMap.get("t4");

        assertThat(t4.getSuccessorIds()).isEqualTo(Set.of(ids.get(4)));
        assertThat(JacksonUtil.fromJson(t4.getInput(), StringTaskInput.class).getTaskId()).isEqualTo(t4.getId());
        assertThat(t4.getExecutionKey()).isEqualTo("top.ilovemyhome.peanotes.backend.common.task.impl.execution.LongRunningExecution");
        assertThat(t4.getTimeout()).isEqualTo(1L);
        assertThat(t4.getTimeoutUnit()).isEqualTo(TimeUnit.SECONDS);

        taskDagService.loadAndStart(orderKey);
        ThreadUtils.sleepQuietly(Duration.ofSeconds(2));
        nameMap = queryAndReturnMap(orderKey);
        assertThat(nameMap.get("t1").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t2").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t3").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t4").getStatus()).isEqualTo(TaskStatus.TIMEOUT);
        assertThat(nameMap.get("t5").getStatus()).isEqualTo(TaskStatus.INIT);
    }

    @Test
    public void testTaskOrderAndRunForSyncException() {
        String orderKey = "FOO_DAILY_20240718_ERROR_SYNC";
        TaskOrder fooDaily20240718 = TaskOrder.builder()
            .withName("foo")
            .withOrderType(OrderType.Daily)
            .withKey(orderKey)
            .withAttributes(Map.of("bizDate", "20240718", "runType", "SYNC", "error", "ERROR"))
            .build();
        taskDagService.deleteOrderByKey(orderKey, true);
        taskDagService.createOrder(fooDaily20240718);

        List<Long> ids = taskDagService.getNextTaskIds(5);
        TaskRecord t1 = createT1(ids.getFirst(), fooDaily20240718, false, "t1Input", Set.of(ids.get(1), ids.get(2)));
        TaskRecord t2 = createT2(ids.get(1), fooDaily20240718, false, "t2Input", null);
        TaskRecord t3 = createT3(ids.get(2), fooDaily20240718, false, "error_t3Input", Set.of(ids.get(3), ids.get(4)));
        TaskRecord t4 = createT4(ids.get(3), fooDaily20240718, false, "t4Input", Set.of(ids.get(4)), 10L);
        TaskRecord t5 = createT5(ids.get(4), fooDaily20240718, false, "t5Input", null);
        taskDagService.createTasks(List.of(t1, t2, t3, t4, t5));

        assertThat(fooDaily20240718.getKey()).isEqualTo(orderKey);
        Map<String, TaskRecord> nameMap = queryAndReturnMap(orderKey);
        t3 = nameMap.get("t3");

        assertThat(t3.getSuccessorIds()).isEqualTo(Set.of(ids.get(3), ids.get(4)));
        assertThat(JacksonUtil.fromJson(t3.getInput(), StringTaskInput.class).getTaskId()).isEqualTo(t3.getId());
        assertThat(t3.getExecutionKey()).isEqualTo("top.ilovemyhome.peanotes.backend.common.task.impl.execution.ConditionExceptionalExecution");
        assertThat(t3.getTimeout()).isEqualTo(10L);
        assertThat(t4.getTimeoutUnit()).isEqualTo(TimeUnit.SECONDS);

        taskDagService.loadAndStart(orderKey);
        ThreadUtils.sleepQuietly(Duration.ofSeconds(1));
        nameMap = queryAndReturnMap(orderKey);
        assertThat(nameMap.get("t1").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t2").getStatus()).isEqualTo(TaskStatus.SUCCESS);
        assertThat(nameMap.get("t3").getStatus()).isEqualTo(TaskStatus.ERROR);
        assertThat(nameMap.get("t3").getFailReason()).isEqualTo("java.lang.RuntimeException: Mocked exception");
        assertThat(nameMap.get("t3").getEndDt()).isNotNull();
        assertThat(nameMap.get("t4").getStatus()).isEqualTo(TaskStatus.INIT);
        assertThat(nameMap.get("t5").getStatus()).isEqualTo(TaskStatus.INIT);
    }

    private TaskRecord createT1(Long id, TaskOrder taskOrder, boolean async, String input, Set<Long> successorIds) {
        return TaskRecord.builder()
            .withId(id)
            .withOrderKey(taskOrder.getKey())
            .withName("t1")
            .withAsync(async)
            .withInput(JacksonUtil.toJson(new StringTaskInput(id, input, Map.of("p1", "v1", "p2", "v2"))))
            .withDescription("T1 print log task")
            .withExecutionKey("top.ilovemyhome.peanotes.backend.common.task.impl.execution.PrintInputTaskExecution")
            .withSuccessorIds(successorIds)
            .withStatus(TaskStatus.INIT)
            .withTimeoutUnit(TimeUnit.HOURS)
            .withTimeout(1L)
            .build();
    }

    private TaskRecord createT2(Long id, TaskOrder taskOrder, boolean async, String input, Set<Long> successorIds) {
        return TaskRecord.builder()
            .withId(id)
            .withOrderKey(taskOrder.getKey())
            .withName("t2")
            .withDescription("T2 print log task")
            .withAsync(async)
            .withInput(JacksonUtil.toJson(new StringTaskInput(id, input, Map.of("p1", "v1", "p2", "v2"))))
            .withStatus(TaskStatus.INIT)
            .withTimeoutUnit(TimeUnit.MINUTES)
            .withSuccessorIds(successorIds)
            .withTimeout(1L)
            .withExecutionKey("top.ilovemyhome.peanotes.backend.common.task.impl.execution.PrintInputTaskExecution")
            .build();
    }

    private TaskRecord createT3(Long id, TaskOrder taskOrder, boolean async, String input, Set<Long> successorIds) {
        return TaskRecord.builder()
            .withId(id)
            .withOrderKey(taskOrder.getKey())
            .withName("t3")
            .withDescription("T3 conditional exception task")
            .withAsync(async)
            .withInput(JacksonUtil.toJson(new StringTaskInput(id, input, Map.of("p1", "v1", "p2", "v2"))))
            .withStatus(TaskStatus.INIT)
            .withTimeoutUnit(TimeUnit.SECONDS)
            .withTimeout(10L)
            .withSuccessorIds(successorIds)
            .withExecutionKey("top.ilovemyhome.peanotes.backend.common.task.impl.execution.ConditionExceptionalExecution")
            .build();
    }

    private TaskRecord createT4(Long id, TaskOrder taskOrder, boolean async, String input, Set<Long> successorIds, Long timeout) {
        return TaskRecord.builder()
            .withId(id)
            .withOrderKey(taskOrder.getKey())
            .withName("t4")
            .withAsync(async)
            .withDescription("T4 long running task")
            .withInput(JacksonUtil.toJson(new StringTaskInput(id, input, Map.of("p1", "v1", "p2", "v2"))))
            .withStatus(TaskStatus.INIT)
            .withTimeout(timeout)
            .withTimeoutUnit(TimeUnit.SECONDS)
            .withSuccessorIds(successorIds)
            .withExecutionKey("top.ilovemyhome.peanotes.backend.common.task.impl.execution.LongRunningExecution")
            .build();
    }

    private TaskRecord createT5(Long id, TaskOrder taskOrder, boolean async, String input, Set<Long> successorIds) {
        return TaskRecord.builder()
            .withId(id)
            .withOrderKey(taskOrder.getKey())
            .withName("t5")
            .withAsync(async)
            .withDescription("T5 print log task")
            .withInput(JacksonUtil.toJson(new StringTaskInput(id, input, Map.of("p1", "v1", "p2", "v2"))))
            .withStatus(TaskStatus.INIT)
            .withTimeoutUnit(TimeUnit.SECONDS)
            .withTimeout(10L)
            .withSuccessorIds(successorIds)
            .withExecutionKey("top.ilovemyhome.peanotes.backend.common.task.impl.execution.PrintInputTaskExecution")
            .build();
    }

    private Map<String, TaskRecord> queryAndReturnMap(String orderKey) {
        return taskDagService.findTaskByOrderKey(orderKey)
            .stream().collect(Collectors.toMap(TaskRecord::getName, Function.identity()));
    }

    @BeforeAll
    public static void init() {
        dataSourceFactory = TestUtils.getDataSourceFactory();
        taskContext = FooTaskContext.getInstance(dataSourceFactory.getJdbi());
        new TaskRecordDaoJdbiImpl(taskContext);
        new TaskOrderDaoJdbiImpl(taskContext);
        new FooTaskFactoryImpl(taskContext);
        taskDagService = new TaskDagServiceImpl(taskContext);
    }

    @AfterAll
    public static void destroy(){
        dataSourceFactory.getJdbi().useTransaction(h -> {
            h.execute("truncate table t_task_order");
            h.execute("truncate table t_task");
        });
    }


    private static TaskContext taskContext;
    private static TaskDagService taskDagService;
    private static SimpleDataSourceFactory dataSourceFactory;


    private static final Logger LOGGER = LoggerFactory.getLogger(TaskDagServiceTest.class);

}
