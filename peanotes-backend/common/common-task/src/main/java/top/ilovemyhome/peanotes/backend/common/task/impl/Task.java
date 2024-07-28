package top.ilovemyhome.peanotes.backend.common.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.task.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class Task<I, O> implements Runnable {

    //Static
    private final Long id;
    private final String orderKey;
    private final String name;
    private final TaskExecution<I, O> taskExecution;
    private final Long timeout;
    private final TimeUnit timeoutUnit;
    private final LocalDateTime createDt;

    private TaskInput<I> input;
    private Set<Long> successorIds;
    private TaskOutput<O> output;
    private Set<Task<I, O>> successorTasks;
    private Set<Task<I, O>> priorTasks;
    private TaskStatus taskStatus = TaskStatus.INIT;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    protected transient TaskContext<I, O> taskContext;
    private LocalDateTime lastUpdateDt;

    Task(Long id, TaskContext<I, O> taskContext, String orderKey, String name, TaskInput<I> input
        , Long timeout, TimeUnit timeoutUnit, TaskExecution<I, O> taskExecution) {
        LocalDateTime now = LocalDateTime.now();
        this.id = id;
        this.taskContext = taskContext;
        this.orderKey = orderKey;
        this.name = name;
        this.input = input;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
        this.taskExecution = taskExecution;
        this.createDt = now;
        this.lastUpdateDt = now;
    }

    void start() {
        LocalDateTime now = LocalDateTime.now();
        this.input = input;
        this.startDt = now;
        this.taskStatus = TaskStatus.RUNNING;
        this.lastUpdateDt = now;
        taskContext.getTaskDao().start(id, this.input, now);
    }

    void failure(TaskStatus newStatus, TaskOutput<O> output) {
        if (newStatus == TaskStatus.SUCCESS) {
            throw new IllegalArgumentException("The task has already been successful");
        }
        LocalDateTime now = LocalDateTime.now();
        this.taskStatus = newStatus;
        this.output = output;
        this.endDt = now;
        this.lastUpdateDt = now;
        taskContext.getTaskDao().stop(id, newStatus, output, now);
    }

    boolean isReady() {
        boolean result = true;
        if (Objects.isNull(priorTasks) || priorTasks.isEmpty()) {
            return result;
        }
        for (Task<I, O> task : priorTasks) {
            if (task.getTaskStatus() != TaskStatus.SUCCESS) {
                result = false;
                break;
            }
        }
        return result;
    }

    void error(TaskOutput<O> output) {
        failure(TaskStatus.ERROR, output);
    }

    void unknown(TaskOutput<O> output) {
        failure(TaskStatus.UNKNOWN, output);
    }

    void timeout(TaskOutput<O> output) {
        failure(TaskStatus.TIMEOUT, output);
    }

    void success(TaskOutput<O> output) {
        LocalDateTime now = LocalDateTime.now();
        this.output = output;
        this.endDt = now;
        this.lastUpdateDt = now;
        this.taskStatus = TaskStatus.SUCCESS;
        taskContext.getTaskDao().stop(id, TaskStatus.SUCCESS, output, now);
        LOGGER.info("OrderId={}, id={}, name={} execute successfully.", orderKey, id, name);
        Set<Task<I, O>> successors = getSuccessorTasks();
        if (Objects.nonNull(successors) && !successors.isEmpty()) {
            successors.forEach(s -> {
                if (s.isReady()) {
                    taskContext.getThreadPool().submit(s);
                }
            });
        }
    }

    private void addPriorTask(Task<I, O> task) {
        if (Objects.isNull(priorTasks)) {
            priorTasks = new HashSet<>();
        }
        this.priorTasks.add(task);
    }

    void addSuccessorTask(Task<I, O> successorTask, boolean updateIds) {
        if (Objects.isNull(this.successorTasks)) {
            this.successorTasks = new HashSet<>(1);
        }
        this.successorTasks.add(successorTask);
        successorTask.addPriorTask(this);
        if (updateIds) {
            if (Objects.isNull(this.successorIds)) {
                this.successorIds = new HashSet<>();
            }
            this.successorIds.add(successorTask.getId());
        }
    }


    public Long getId() {
        return id;
    }

    public String getOrderKey() {
        return orderKey;
    }

    public String getName() {
        return name;
    }

    public TaskInput<I> getInput() {
        return input;
    }

    public TaskExecution<I, O> getTaskExecution() {
        return taskExecution;
    }

    public Long getTimeout() {
        return timeout;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    public void setSuccessorIds(Set<Long> successorIds) {
        this.successorIds = successorIds;
    }

    public Set<Long> getSuccessorIds() {
        return successorIds;
    }

    public TaskOutput<O> getOutput() {
        return output;
    }

    public Set<Task<I, O>> getSuccessorTasks() {
        return successorTasks;
    }

    public Set<Task<I, O>> getPriorTasks() {
        return priorTasks;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public LocalDateTime getStartDt() {
        return startDt;
    }

    public LocalDateTime getEndDt() {
        return endDt;
    }

    public LocalDateTime getLastUpdateDt() {
        return lastUpdateDt;
    }

    @Override
    public String toString() {
        return "AbstractTask{" +
            "id=" + id +
            ", orderId='" + orderKey + '\'' +
            ", name='" + name + '\'' +
            ", input=" + input +
            ", taskExecution=" + taskExecution +
            ", timeout=" + timeout +
            ", timeoutUnit=" + timeoutUnit +
            ", createDt=" + createDt +
            ", successorIds=" + successorIds +
            ", output=" + output +
            ", taskStatus=" + taskStatus +
            ", startTs=" + startDt +
            ", endTs=" + endDt +
            ", lastUpdateDt=" + lastUpdateDt +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task<?, ?> that = (Task<?, ?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

}
