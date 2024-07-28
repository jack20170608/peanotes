package top.ilovemyhome.peanotes.backend.common.task;

import top.ilovemyhome.peanotes.backend.common.task.impl.AsyncTask;
import top.ilovemyhome.peanotes.backend.common.task.impl.SyncTask;
import top.ilovemyhome.peanotes.backend.common.task.impl.Task;

import java.util.concurrent.TimeUnit;

public class TaskBuilder<I, O> {
    private boolean asyncFlag = true;

    private Long id;
    private TaskContext<I, O> taskContext;
    private String orderKey;
    private String name;
    private TaskExecution<I, O> execution;
    private TaskInput<I> input;
    private Long timeout;
    private TimeUnit timeoutUnit;

    public static TaskBuilder builder() {
        return new TaskBuilder();
    }

    public TaskBuilder<I, O> withId(Long id) {
        this.id = id;
        return this;
    }

    public TaskBuilder<I, O> withTaskContext(TaskContext taskContext) {
        this.taskContext = taskContext;
        return this;
    }

    public TaskBuilder<I, O> withOrderKey(String orderKey) {
        this.orderKey = orderKey;
        return this;
    }

    public TaskBuilder<I, O> withName(String name) {
        this.name = name;
        return this;
    }

    public TaskBuilder<I, O> withExecution(TaskExecution<I, O> execution) {
        this.execution = execution;
        return this;
    }

    public TaskBuilder<I, O> withInput(TaskInput<I> input) {
        this.input = input;
        return this;
    }

    public TaskBuilder<I, O> withTimeout(Long timeout) {
        this.timeout = timeout;
        return this;
    }

    public TaskBuilder<I, O> withTimeoutUnit(TimeUnit timeoutUnit) {
        this.timeoutUnit = timeoutUnit;
        return this;
    }

    public TaskBuilder<I, O> withAsyncFlag(boolean asyncFlag) {
        this.asyncFlag = asyncFlag;
        return this;
    }

    public Task<I, O> build() {
        Task<I, O> task = null;
        if (this.asyncFlag) {
            task = new AsyncTask<I, O>(id, taskContext, orderKey, name, input, timeout, timeoutUnit, execution);
        } else {
            task = new SyncTask<>(id, taskContext, orderKey, name, input
                , timeout , timeoutUnit, execution);
        }
        return task;
    }
}
