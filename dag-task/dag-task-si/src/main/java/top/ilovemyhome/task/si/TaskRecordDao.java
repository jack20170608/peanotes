package top.ilovemyhome.task.si;

import top.ilovemyhome.peanotes.commons.jdbi.dao.BaseDao;
import top.ilovemyhome.task.si.domain.Task;
import top.ilovemyhome.task.si.domain.TaskRecord;
import top.ilovemyhome.task.si.enums.TaskStatus;

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
