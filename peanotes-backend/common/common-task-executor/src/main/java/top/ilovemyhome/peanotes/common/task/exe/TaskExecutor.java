package top.ilovemyhome.peanotes.common.task.exe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.exe.domain.*;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.ExecutorBlockStrategyEnum;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.TaskType;
import top.ilovemyhome.peanotes.common.task.exe.handler.ScriptTaskHandler;
import top.ilovemyhome.peanotes.common.task.exe.processor.RegistryProcessor;
import top.ilovemyhome.peanotes.common.task.exe.processor.TaskCallbackProcessor;
import top.ilovemyhome.peanotes.common.task.exe.processor.TaskProcessor;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public interface TaskExecutor extends LifeCycle {

    TaskExecutorContext getContext();

    //The 2 internal components
    TaskCallbackProcessor getCallbackProcessor();

    RegistryProcessor getRegistryProcessor();

    static TaskExecutor of(TaskExecutorContext context) {
        return new TaskExecutorImpl(context);
    }

    String beat();

    TaskResponse idleBeat(IdleBeatParam idleBeatParam);

    TaskResponse run(TriggerParam triggerParam);

    TaskResponse kill(KillParam killParam);

    void log(LogParam logParam);

    TaskProcessor registTaskThread(Long jobId, TaskHandler handler, String removeOldReason);

    TaskProcessor removeTaskThread(Long jobId, String removeOldReason);

    TaskProcessor loadTaskThread(Long jobId);

    List<TaskAdmin> getTaskAdmins();

    boolean isRegistered();
}


class TaskExecutorImpl implements TaskExecutor {

    public TaskExecutorImpl(TaskExecutorContext taskExecutorContext) {
        this.taskExecutorContext = taskExecutorContext;
        taskAdmins = this.taskExecutorContext.getListOfAdmin().stream().filter(a -> {
            return Objects.nonNull(a)
                && !a.isBlank();
        }).map(a -> {
            return TaskAdmin.builder()
                .withAdminServerUrl(a)
                .withAccessToken(null)
                .build();
        }).collect(Collectors.toList());

        this.registryProcessor = RegistryProcessor.builder()
            .withTaskExecutor(this)
            .build();

        this.taskCallbackProcessor = TaskCallbackProcessor.builder()
            .withTaskExecutor(this)
            .build();
    }

    @Override
    public TaskExecutorContext getContext() {
        return this.taskExecutorContext;
    }

    @Override
    public TaskCallbackProcessor getCallbackProcessor() {
        return this.taskCallbackProcessor;
    }

    @Override
    public RegistryProcessor getRegistryProcessor() {
        return this.registryProcessor;
    }

    @Override
    public String beat() {
        return "PONG";
    }

    @Override
    public TaskResponse idleBeat(IdleBeatParam idleBeatParam) {
        TaskProcessor jobThread = this.loadTaskThread(idleBeatParam.jobId());
        if (jobThread != null && jobThread.isRunningOrHasQueue()) {
            return TaskResponse.of(500, "job thread is running or has trigger queue.", null);
        }
        return TaskResponse.SUCCESS;
    }

    @Override
    public TaskResponse run(TriggerParam triggerParam) {
        TaskProcessor taskProcessor = this.loadTaskThread(triggerParam.jobId());
        TaskHandler taskHandler = taskProcessor != null ? taskProcessor.getTaskHandler() : null;
        TaskType taskType = triggerParam.taskType();
        String removeOldReason = null;
        switch (taskType) {
            case BAEN -> {
                TaskHandler newTaskHandler = taskExecutorContext.getTaskHandler(triggerParam.executorHandler());
                if (taskProcessor != null && taskHandler != newTaskHandler) {
                    // change handler, need kill old thread
                    removeOldReason = "change task handler or glue type, and terminate the old thread.";
                    taskProcessor = null;
                    taskHandler = null;
                }
                if (taskHandler == null) {
                    taskHandler = newTaskHandler;
                    if (taskHandler == null) {
                        return new TaskResponse(404, "Task handler [" + triggerParam.executorHandler() + "] not found.", null);
                    }
                }
            }
            case SHELL, PYTHON, PHP, NODEJS, POWERSHELL -> {
                // valid old jobThread
                if (taskProcessor != null &&
                    !(taskProcessor.getTaskHandler() instanceof ScriptTaskHandler
                        && ((ScriptTaskHandler) taskProcessor.getTaskHandler()).getLastUpdateDt() == triggerParam.scriptUpdatetime())) {
                    // change script or gluesource updated, need kill old thread
                    removeOldReason = "change job source or glue type, and terminate the old job thread.";
                    taskProcessor = null;
                    taskHandler = null;
                }
                // valid handler
                if (taskHandler == null) {
                    taskHandler = new ScriptTaskHandler(this.taskExecutorContext, triggerParam.jobId()
                        , triggerParam.scriptUpdatetime()
                        , triggerParam.scriptSource()
                        , triggerParam.taskType());
                }
            }
            case GLUE_GROOVY -> {
                throw new TaskExecuteException("Task type is null or unsupported task type.");
            }
            case null, default -> {
                throw new TaskExecuteException("Task type is null or unsupported task type.");
            }
        }

        // executor block strategy
        if (taskProcessor != null) {
            ExecutorBlockStrategyEnum blockStrategy = triggerParam.executorBlockStrategy();
            switch (blockStrategy){
                case DISCARD_LATER -> {
                    if (taskProcessor.isRunningOrHasQueue()){
                        return TaskResponse.of(400, "block strategy effect："+ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle(), null);
                    }
                }
                case COVER_EARLY -> {
                    if (taskProcessor.isRunningOrHasQueue()) {
                        removeOldReason = "block strategy effect：" + ExecutorBlockStrategyEnum.COVER_EARLY.getTitle();
                        taskProcessor = null;
                    }
                }
            }
        }
        if (taskProcessor == null) {
            taskProcessor = this.registTaskThread(triggerParam.jobId(), taskHandler, removeOldReason);
        }
        // push data to queue
        taskProcessor.pushTriggerQueue(triggerParam);
        return TaskResponse.SUCCESS;
    }


    @Override
    public TaskResponse kill(KillParam killParam) {
        TaskProcessor jobThread = this.loadTaskThread(killParam.jobId());
        if (jobThread != null) {
            this.removeTaskThread(killParam.jobId(), "scheduling center kill job.");
            return TaskResponse.SUCCESS;
        }
        return TaskResponse.ofSuccess("job thread already killed.");
    }

    @Override
    public void log(LogParam logParam) {
        return;
    }


    private final static ConcurrentMap<Long, TaskProcessor> jobThreadRepository = new ConcurrentHashMap<>();

    @Override
    public TaskProcessor registTaskThread(Long jobId, TaskHandler
        handler, String removeOldReason) {
        TaskProcessor newTaskProcessor = TaskProcessor.builder()
            .withTaskExecutor(this)
            .withJobId(jobId)
            .withTaskHandler(handler)
            .build();

        newTaskProcessor.start();
        LOGGER.info("Register success, jobId:{}, handler:{}", jobId, handler);
        TaskProcessor oldTaskProcessor = jobThreadRepository.put(jobId, newTaskProcessor);
        if (oldTaskProcessor != null) {
            oldTaskProcessor.stop();
            oldTaskProcessor.interrupt();
        }
        return newTaskProcessor;
    }

    @Override
    public TaskProcessor removeTaskThread(Long jobId, String
        removeOldReason) {
        TaskProcessor oldTaskProcessor = jobThreadRepository.remove(jobId);
        if (oldTaskProcessor != null) {
            oldTaskProcessor.stop();
            oldTaskProcessor.interrupt();
            return oldTaskProcessor;
        }
        return null;
    }

    @Override
    public TaskProcessor loadTaskThread(Long jobId) {
        return jobThreadRepository.get(jobId);
    }

    @Override
    public List<TaskAdmin> getTaskAdmins() {
        return taskAdmins;
    }

    @Override
    public boolean isRegistered() {
        return this.registryProcessor.isRegistered();
    }

    private final RegistryProcessor registryProcessor;
    private final TaskCallbackProcessor taskCallbackProcessor;
    private final TaskExecutorContext taskExecutorContext;
    private final List<TaskAdmin> taskAdmins;


    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutorImpl.class);


    @Override
    public State getState() {
        return stateRef.get();
    }

    @Override
    public void start() {
        if (stateRef.compareAndSet(State.INITIALIZED, State.STARTING)) {
            try {
                this.taskCallbackProcessor.start();
                this.registryProcessor.start();
            } catch (Throwable throwable) {
                stateRef.set(State.STOPPED);
                throw throwable;
            }
            stateRef.set(State.STARTED);
        }
    }


    @Override
    public void stop(Duration timeoutDuration) {
        if (stateRef.compareAndSet(State.STARTED, State.STOPPING)) {
            this.registryProcessor.stop();
            this.taskCallbackProcessor.stop();
            stateRef.set(State.STOPPED);
        }
    }

    private final AtomicReference<State> stateRef = new AtomicReference<>(State.INITIALIZED);

}
