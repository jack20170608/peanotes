package top.ilovemyhome.task.si.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.task.si.TaskContext;
import top.ilovemyhome.task.si.TaskExecution;
import top.ilovemyhome.task.si.TaskInput;
import top.ilovemyhome.task.si.TaskOutput;
import top.ilovemyhome.task.si.enums.TaskStatus;

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
    private TaskStatus taskStatus ;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    protected transient TaskContext taskContext;
    private LocalDateTime lastUpdateDt;

    protected Task(Long id, TaskContext taskContext, String orderKey, String name, TaskInput<I> input
        , TaskStatus taskStatus, Long timeout, TimeUnit timeoutUnit, TaskExecution<I, O> taskExecution) {
        LocalDateTime now = LocalDateTime.now();
        this.id = id;
        this.taskContext = taskContext;
        this.orderKey = orderKey;
        this.name = name;
        this.input = input;
        this.taskStatus = taskStatus;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
        this.taskExecution = taskExecution;
        this.createDt = now;
        this.lastUpdateDt = now;
    }

    protected void start() {
        LocalDateTime now = LocalDateTime.now();
        this.startDt = now;
        this.taskStatus = TaskStatus.RUNNING;
        this.lastUpdateDt = now;
        taskContext.getTaskRecordDao().start(id, this.input, now);
    }

    public void failure(TaskStatus newStatus, TaskOutput<O> output) {
        if (newStatus == TaskStatus.SUCCESS) {
            throw new IllegalArgumentException("The status should not success as your call failure method!!");
        }
        LocalDateTime now = LocalDateTime.now();
        this.taskStatus = newStatus;
        this.output = output;
        this.endDt = now;
        this.lastUpdateDt = now;
        taskContext.getTaskRecordDao().stop(id, newStatus, output, now);
    }

    public boolean isReady() {
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

    protected void error(TaskOutput<O> output) {
        failure(TaskStatus.ERROR, output);
    }

    protected void unknown(TaskOutput<O> output) {
        failure(TaskStatus.UNKNOWN, output);
    }

    protected void timeout(TaskOutput<O> output) {
        failure(TaskStatus.TIMEOUT, output);
    }

    public void success(TaskOutput<O> output) {
        LocalDateTime now = LocalDateTime.now();
        this.output = output;
        this.endDt = now;
        this.lastUpdateDt = now;
        this.taskStatus = TaskStatus.SUCCESS;
        taskContext.getTaskRecordDao().stop(id, TaskStatus.SUCCESS, output, now);
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

    public void skip(TaskOutput<O> output) {
        LocalDateTime now = LocalDateTime.now();
        this.output = output;
        this.endDt = now;
        this.lastUpdateDt = now;
        this.taskStatus = TaskStatus.SKIPPED;
        taskContext.getTaskRecordDao().stop(id, TaskStatus.SKIPPED, output, now);
        LOGGER.info("OrderId={}, id={}, name={} execute skipped.", orderKey, id, name);
        Set<Task<I, O>> successors = getSuccessorTasks();
        if (Objects.nonNull(successors) && !successors.isEmpty()) {
            successors.forEach(s -> {
                s.skip(output);
            });
        }
    }

    public void addPriorTask(Task<I, O> task) {
        if (Objects.isNull(priorTasks)) {
            priorTasks = new HashSet<>();
        }
        this.priorTasks.add(task);
    }

    public void addSuccessorTask(Task<I, O> successorTask, boolean updateIds) {
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
