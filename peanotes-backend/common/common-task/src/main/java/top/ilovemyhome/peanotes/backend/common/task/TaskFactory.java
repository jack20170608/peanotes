package top.ilovemyhome.peanotes.backend.common.task;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.task.impl.Task;

import java.util.List;

public interface TaskFactory<I,O> {

    Logger LOGGER = LoggerFactory.getLogger(TaskFactory.class);

    List<Task<I,O>> createTasksForOrder(TaskOrder taskOrder);

    default TaskExecution<I,O> createTaskForExecution(String executionKey){
        TaskExecution<I, O> result = null;
        try {
            Class executionClass = Class.forName(executionKey);
            result = (TaskExecution<I, O>)executionClass.newInstance();
        } catch (Throwable t){
            LOGGER.error(t.getMessage(), t);
        }
        return result;
    }
}
