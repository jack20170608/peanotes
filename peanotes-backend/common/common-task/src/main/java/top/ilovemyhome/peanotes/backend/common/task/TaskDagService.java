package top.ilovemyhome.peanotes.backend.common.task;


import top.ilovemyhome.peanotes.backend.common.task.impl.Task;

import java.util.List;

public interface TaskDagService<I, O> {

    boolean isOrdered(TaskOrder order);

    List<Task<I, O>> order(TaskOrder order);

    void start(TaskOrder order);

    List<Task<I, O>> orderAndStart(TaskOrder order);

    boolean isSuccess(TaskOrder order);

    List<Task<I, O>> checkStatus(TaskOrder order);

    void receiveTaskEvent(Long taskId, TaskStatus newStatus, TaskOutput<O> output);

}
