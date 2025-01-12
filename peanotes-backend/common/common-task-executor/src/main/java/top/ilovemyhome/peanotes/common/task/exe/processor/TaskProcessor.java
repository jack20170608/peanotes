package top.ilovemyhome.peanotes.common.task.exe.processor;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.exe.*;
import top.ilovemyhome.peanotes.common.task.exe.domain.HandleCallbackParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.HandlerStatus;
import top.ilovemyhome.peanotes.common.task.exe.domain.TriggerParam;
import top.ilovemyhome.peanotes.common.task.exe.handler.TaskHandler;
import top.ilovemyhome.peanotes.common.task.exe.helper.TaskHelper;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static top.ilovemyhome.peanotes.common.task.exe.Constants.MAX_IDLE_POLL_TIMES;
import static top.ilovemyhome.peanotes.common.task.exe.TaskExecutor.CONTEXT;

public interface TaskProcessor extends Runnable, LifeCycle {


    void pushTriggerQueue(TriggerParam triggerParam);

    void interrupt();

    boolean isRunningOrHasQueue();

    TaskHandler getTaskHandler();


    static Builder builder() {
        return new Builder();
    }

    class Builder {
        TaskExecutor taskExecutor;
        Long jobId;
        TaskHandler taskHandler;

        public Builder withTaskExecutor(TaskExecutor taskExecutor) {
            this.taskExecutor = taskExecutor;
            return this;
        }

        public Builder withJobId(Long jobId) {
            this.jobId = jobId;
            return this;
        }

        public Builder withTaskHandler(TaskHandler taskHandler) {
            this.taskHandler = taskHandler;
            return this;
        }

        public TaskProcessor build() {
            return new TaskProcessorImpl(taskExecutor, jobId, taskHandler);
        }
    }
}

class TaskProcessorImpl implements TaskProcessor {

    //The logger
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskProcessor.class);

    //The depend resource
    private final TaskExecutor taskExecutor;
    private final Long taskId;
    private final TaskHandler handler;
    private final AtomicReference<State> stateRef = new AtomicReference<>(State.INITIALIZED);

    private final TaskCallbackProcessor triggerCallbackProcessor;
    private LinkedBlockingQueue<TriggerParam> triggerQueue;
    private Set<Long> triggerLogIdSet;        // avoid repeat trigger for the same TRIGGER_LOG_ID
    private Thread workingThread;


    private volatile boolean stopFlag = false;
    private String stopReason;

    private boolean running = false;    // if running job
    private int idleTimes = 0;          // idle times


    public TaskProcessorImpl(TaskExecutor taskExecutor, Long taskId, TaskHandler handler) {
        this.taskExecutor = taskExecutor;
        this.triggerCallbackProcessor = taskExecutor.getCallbackProcessor();
        this.taskId = taskId;
        this.handler = handler;
        this.triggerQueue = new LinkedBlockingQueue<>();
        this.triggerLogIdSet = Collections.synchronizedSet(new HashSet<>());
    }

    @Override
    public void pushTriggerQueue(TriggerParam triggerParam) {
        // avoid repeat
        if (triggerLogIdSet.contains(triggerParam.logId())) {
            LOGGER.info(">>>>>>>>>>> repeat trigger job, logId:{}", triggerParam.logId());
            throw new TaskExecuteException("repeat trigger job, logId:" + triggerParam.logId());
        }
        triggerLogIdSet.add(triggerParam.logId());
        triggerQueue.add(triggerParam);
    }

    @Override
    public void start() {
        if (stateRef.compareAndSet(State.INITIALIZED, State.STARTING)) {
            try {
                validate();
                LOGGER.info("Starting the task processor.");
                workingThread = new Thread(this, this.getClass().getSimpleName());
                workingThread.start();
            } catch (Throwable throwable) {
                stateRef.set(State.STOPPED);
                throw throwable;
            }
            stateRef.set(State.STARTED);
        }
    }

    @Override
    public void interrupt() {
        if (Objects.nonNull(this.workingThread) && this.workingThread.isAlive()) {
            this.workingThread.interrupt();
        }
    }

    @Override
    public void stop(Duration timeoutDuration) {
        if (stateRef.compareAndSet(State.STARTED, State.STOPPING)) {
            LOGGER.info("Stopping the task processor.");
            this.stopFlag = true;
            this.stopReason = "Stop";
            stateRef.set(State.STOPPED);
        }
    }

    @Override
    public State getState() {
        return stateRef.get();
    }


    @Override
    public boolean isRunningOrHasQueue() {
        return running || triggerQueue.size() > 0;
    }

    @Override
    public TaskHandler getTaskHandler() {
        return this.handler;
    }

    private void validate() {
        if (Objects.isNull(this.taskExecutor)
            || Objects.isNull(this.taskId)
            || Objects.isNull(this.handler)
        ) {
            throw new IllegalArgumentException("The task executor or task id is null or handler is null.");
        }
    }

    private void resetIdleTimes() {
        this.idleTimes = 0;
    }


    private void processTrigger(TriggerParam triggerParam) {
        triggerLogIdSet.remove(triggerParam.logId());
        //1. create new task context and attached to the thread
        TaskContext taskContext = TaskContext.builder()
            .withTaskExecutor(taskExecutor)
            .withTaskId(triggerParam.jobId())
            .withLogId(triggerParam.logId())
            .withTaskParam(triggerParam.executorParams())
            .withTaskName(triggerParam.executorHandler())
            .withShardIndex(triggerParam.broadcastIndex())
            .withShardTotal(triggerParam.broadcastTotal())
            .build();
        CONTEXT.set(taskContext);
        //Attach the log appender
        Logger logger = taskContext.getLogger();
        //Customise the logger appender
        logger.info("----------- Task execute start ----------------- ");
        logger.info("Param is {}." ,taskContext.getTaskParam());

        //2. check if have timeout parameter setup
        Duration timeoutDuration = triggerParam.executorTimeout();
        try {
            if (Objects.nonNull(timeoutDuration) && timeoutDuration.toSeconds() > 0L) {
                //should not use CompletableFuture
                Thread futureThread = null;
                boolean result;
                try {
                    FutureTask<Boolean> futureTask = new FutureTask<>(() -> {
                        handler.handle();
                        return true;
                    });
                    String threadName = Thread.currentThread().getName();
                    futureThread = new Thread(futureTask, threadName + "_SUB");
                    futureThread.start();
                    result = futureTask.get(timeoutDuration.toSeconds(), TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    logger.warn("-----------Task execute timeout-----------------");
                    throw e;
                } finally {
                    futureThread.interrupt();
                }
                logger.info("Task execute result is {}", result);
                logger.info("----------- Task execute successfully ----------------- ");
            } else {
                handler.handle();
            }
        } catch (Throwable t) {
            logger.error("Task execution failure.", t);
            logger.info("----------- Task execute failure ----------------- ");
            throw new TaskExecuteException("Task execute failure", t);
        }
    }

    @Override
    public void run() {
        // execute
        while (!stopFlag) {
            running = false;
            LOGGER.info("Task processor starting....");
            TriggerParam triggerParam = null;
            //Check if have any pending task to run
            try {
                triggerParam = triggerQueue.poll(Constants.POLL_INTERVAL, TimeUnit.SECONDS);
                if (Objects.nonNull(triggerParam)) {
                    //1.0 reset the idle times
                    resetIdleTimes();
                    processTrigger(triggerParam);
                    TaskHelper.handleSuccess("Successfully executed trigger job, logId:" + triggerParam.logId());
                } else {
                    idleTimes++;
                    LOGGER.info("Task queue is empty idleTimes=[{}].", idleTimes);
                    if (idleTimes > MAX_IDLE_POLL_TIMES && triggerQueue.isEmpty()) {
                        taskExecutor.removeTaskThread(taskId, "executor idle times over limit, shutdown the thread.");
                    }
                }
            } catch (InterruptedException ie) {

            } catch (TaskExecuteException te) {
                Throwable cause = te.getCause();
                if (cause instanceof TimeoutException) {
                    TaskHelper.handleTimeout(te.getMessage());
                } else {
                    TaskHelper.handleServerFail(te.getMessage());
                }
            } finally {
                if (triggerParam != null) {
                    // callback handler info
                    if (!stopFlag) {
                        triggerCallbackProcessor.pushCallBack(new HandleCallbackParam(
                            triggerParam.jobId(),
                            triggerParam.executorHandler(),
                            triggerParam.logId(),
                            triggerParam.logDateTime(),
                            CONTEXT.get().getHandlerStatus().getCode(),
                            CONTEXT.get().getHandlerMessage())
                        );
                    } else {
                        // is killed
                        triggerCallbackProcessor.pushCallBack(new HandleCallbackParam(
                            triggerParam.jobId(),
                            triggerParam.executorHandler(),
                            triggerParam.logId(),
                            triggerParam.logDateTime(),
                            HandlerStatus.SERVER_ERROR.getCode(),
                            stopReason + " [job running, killed]")
                        );
                    }
                }
            }
        }

        // callback trigger request in queue
        while (triggerQueue != null && triggerQueue.size() > 0) {
            TriggerParam triggerParam = triggerQueue.poll();
            if (triggerParam != null) {
                // is killed
                triggerCallbackProcessor.pushCallBack(new HandleCallbackParam(
                    triggerParam.jobId(),
                    triggerParam.executorHandler(),
                    triggerParam.logId(),
                    triggerParam.logDateTime(),
                    HandlerStatus.SERVER_ERROR.getCode(),
                    stopReason + " [job not executed, in the job queue, killed.]")
                );
            }
        }
        LOGGER.info(">>>>>>>>>>> xxl-job JobThread stoped, hashCode:{}", Thread.currentThread());
    }

}
