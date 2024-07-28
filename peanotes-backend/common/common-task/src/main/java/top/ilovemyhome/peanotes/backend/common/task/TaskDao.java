package top.ilovemyhome.peanotes.backend.common.task;

import top.ilovemyhome.peanotes.backend.common.task.impl.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskDao<I,O> {

    //Get the next task id
    Long getNextId();

    List<Task<I, O>> loadTaskForOrder(String orderKey);

    int createTasksForOrder(String orderKey, List<Task<I, O>> listOfTask);

    boolean isOrdered(String orderKey);

    boolean isSuccess(String orderKey);

    int start(Long id, TaskInput<I> input, LocalDateTime startDt);

    int stop(Long id, TaskStatus newStatus, TaskOutput<O> output, LocalDateTime stopDt);

    String getTaskOrderByTaskId(Long taskId);

}
