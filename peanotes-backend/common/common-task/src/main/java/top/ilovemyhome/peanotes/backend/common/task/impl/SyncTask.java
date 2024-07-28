package top.ilovemyhome.peanotes.backend.common.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.task.TaskContext;
import top.ilovemyhome.peanotes.backend.common.task.TaskExecution;
import top.ilovemyhome.peanotes.backend.common.task.TaskInput;
import top.ilovemyhome.peanotes.backend.common.task.TaskOutput;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SyncTask<I, O> extends Task<I, O> {

    private static final long DEFAULT_TIMEOUT_SECONDS = 60;
    private static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    public SyncTask(Long id, TaskContext<I, O> taskContext, String orderKey, String name
        , TaskInput<I> input, Long timeout, TimeUnit timeoutUnit, TaskExecution<I, O> taskExecution) {
        super(id, taskContext, orderKey, name, input, timeout < 1L ? DEFAULT_TIMEOUT_SECONDS : timeout
            , Objects.isNull(timeoutUnit) ? DEFAULT_TIMEOUT_UNIT : timeoutUnit, taskExecution);
    }

    @Override
    public void run() {
        try {
            LOGGER.info("OrderId=[{}], Id=[{}], name=[{}] is running.", getOrderKey(), getId(), getName());
            start();
            CompletableFuture<TaskOutput<O>> cf = CompletableFuture.supplyAsync(() -> this.getTaskExecution().execute(getInput()));
            TaskOutput<O> out = cf.get(this.getTimeout(), this.getTimeoutUnit());
            success(out);
            LOGGER.info("OrderId=[{}], Id=[{}], name=[{}] run successfully.", getOrderKey(), getId(), getName());
        } catch (ExecutionException e) {
            LOGGER.error("OrderId=[{}], Id=[{}], name=[{}] execution failure.", getOrderKey(), getId(), getName());
            error(TaskHelper.createErrorOutput(e));
        } catch (InterruptedException i) {
            LOGGER.error("OrderId=[{}], Id=[{}], name=[{}] execution with unknown status.", getOrderKey(), getId(), getName());
            unknown(TaskHelper.createErrorOutput(i));
        } catch (TimeoutException t) {
            LOGGER.error("OrderId=[{}], Id=[{}], name=[{}] execution timeout.", getOrderKey(), getId(), getName());
            error(TaskHelper.createErrorOutput(t));
            timeout(TaskHelper.createErrorOutput(t));
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncTask.class);
}
