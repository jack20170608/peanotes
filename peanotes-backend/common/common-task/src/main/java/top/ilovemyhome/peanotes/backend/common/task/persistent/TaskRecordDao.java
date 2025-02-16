package top.ilovemyhome.peanotes.backend.common.task.persistent;

import top.ilovemyhome.peanotes.backend.common.db.dao.common.BaseDao;
import top.ilovemyhome.peanotes.backend.common.task.TaskInput;
import top.ilovemyhome.peanotes.backend.common.task.TaskOutput;
import top.ilovemyhome.peanotes.backend.common.task.TaskStatus;
import top.ilovemyhome.peanotes.backend.common.task.impl.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRecordDao extends BaseDao<TaskRecord> {
    //Get the next task id
    Long getNextId();

    int deleteByOrderKey(String orderKey);

    <I, O> List<Task<I, O>> loadTaskForOrder(String orderKey);

    <I, O> int createTasksForOrder(String orderKey, List<Task<I, O>> listOfTask);

    boolean isOrdered(String orderKey);

    boolean isSuccess(String orderKey);

    <I> int start(Long id, TaskInput<I> input, LocalDateTime startDt);

    <O> int stop(Long id, TaskStatus newStatus, TaskOutput<O> output, LocalDateTime stopDt);

    String getTaskOrderByTaskId(Long taskId);


}
