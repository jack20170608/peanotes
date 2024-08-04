package top.ilovemyhome.peanotes.backend.common.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.task.*;

import java.util.concurrent.TimeUnit;

import static top.ilovemyhome.peanotes.backend.common.task.impl.TaskHelper.createErrorOutput;

public class AsyncTask<I, O> extends Task<I, O> {

    public AsyncTask(Long id, TaskContext taskContext, String orderKey, String name, TaskInput<I> input
        , Long timeout, TimeUnit timeoutUnit, TaskExecution<I, O> taskExecution) {
        super(id, taskContext, orderKey, name, input, timeout, timeoutUnit, taskExecution);
    }


    @Override
    public void run() {
        try {
            start();
            TaskOutput<O> out = this.getTaskExecution().execute(getInput());
            if (out != null && out.isSuccessful()) {
                LOGGER.info("OrderId=[{}], Id=[{}], name=[{}] triggered successfully.", getOrderKey(), getId(), getName());
            } else {
                LOGGER.info("OrderId=[{}], Id=[{}], name=[{}] triggered failure.", getOrderKey(), getId(), getName());
                failure(TaskStatus.ERROR, out);
            }
        } catch (Throwable t) {
            LOGGER.error("Run task failed.", t);
            error(createErrorOutput(t));
        }
    }



    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncTask.class);
}
