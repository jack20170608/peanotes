package top.ilovemyhome.peanotes.backend.common.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.task.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FooTaskFactoryImpl implements TaskFactory<String, String> {

    public FooTaskFactoryImpl(TaskContext<String, String> taskContext) {
        this.taskContext = taskContext;
    }

    @Override
    public List<Task<String, String>> createTasksForOrder(TaskOrder taskOrder) {
        String runType = taskOrder.getOtherKeys().get("runType");
        String type = String.format("%s_%s_%s", taskOrder.getName(), taskOrder.getOrderType(), runType).toUpperCase();
        switch (type) {
            case "FOO_DAILY_ASYNC" -> {
                return createFooDailyTasks(taskOrder, true);
            }
            case "FOO_DAILY_SYNC" -> {
                return createFooDailyTasks(taskOrder, false);
            }
            case "FOO_WEEKLY" -> {
                return createFooDailyTasks(taskOrder, false);
            }
            default -> {
                throw new IllegalArgumentException("Don't know how to create tasks.");
            }
        }
    }


    private List<Task<String, String>> createFooDailyTasks(TaskOrder taskOrder, boolean async) {
        List<Task<String, String>> result = new ArrayList<>();
        Task<String, String> t1 = createTask(taskOrder, "t1", async, "t1Input",
            createTaskForExecution("top.ilovemyhome.peanotes.backend.common.task.impl.execution.PrintInputTaskExecution"));
        Task<String, String> t2 = createTask(taskOrder, "t2", async, "t2Input",
            createTaskForExecution("top.ilovemyhome.peanotes.backend.common.task.impl.execution.PrintInputTaskExecution"));
        Task<String, String> t3 = createTask(taskOrder, "t3", async, "t3Input",
            createTaskForExecution("top.ilovemyhome.peanotes.backend.common.task.impl.execution.ConditionExceptionalExecution"));
        Task<String, String> t4 = createTask(taskOrder, "t4", async, "t4Input",
            createTaskForExecution("top.ilovemyhome.peanotes.backend.common.task.impl.execution.LongRunningExecution"));
        Task<String, String> t5 = createTask(taskOrder, "t5", async, "t5Input",
            createTaskForExecution("top.ilovemyhome.peanotes.backend.common.task.impl.execution.PrintInputTaskExecution"));

        result.add(t1);
        result.add(t2);
        result.add(t3);
        result.add(t4);
        result.add(t5);

        createDAG(result);
        return result;
    }

    //0 --> {1, 2}
    //2 --> {3, 4}
    //3 --> {4}
    private void createDAG(List<Task<String, String>> taskList) {
        Task<String, String> t1 = taskList.get(0);
        Task<String, String> t2 = taskList.get(1);
        Task<String, String> t3 = taskList.get(2);
        Task<String, String> t4 = taskList.get(3);
        Task<String, String> t5 = taskList.get(4);

        t1.addSuccessorTask(t2, true);
        t1.addSuccessorTask(t3, true);

        t3.addSuccessorTask(t4, true);
        t3.addSuccessorTask(t5, true);
        t4.addSuccessorTask(t5, true);

    }

    private Task<String, String> createTask(TaskOrder taskOrder, String name, boolean asyncFlag, String input
        , TaskExecution<String, String> taskExecution) {
        return createTask(taskOrder, name, asyncFlag, input, taskExecution, -1L, null);
    }

    private Task<String, String> createTask(TaskOrder taskOrder, String name, boolean asyncFlag, String input
        , TaskExecution<String, String> taskExecution, Long timeout, TimeUnit timeUnit) {
        Long id = taskContext.getTaskDao().getNextId();
        return TaskBuilder.builder()
            .withId(id)
            .withTaskContext(this.taskContext)
            .withOrderKey(taskOrder.getKey())
            .withName(name)
            .withExecution(taskExecution)
            .withInput(new StringTaskInput(taskOrder, input, null))
            .withTimeout(timeout)
            .withTimeoutUnit(timeUnit)
            .withAsyncFlag(asyncFlag)
            .build();
    }

    private final TaskContext<String, String> taskContext;

    private static final Logger LOGGER = LoggerFactory.getLogger(FooTaskFactoryImpl.class);
}
