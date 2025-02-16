package top.ilovemyhome.peanotes.backend.common.task;


import top.ilovemyhome.peanotes.backend.common.task.persistent.TaskOrder;
import top.ilovemyhome.peanotes.backend.common.task.persistent.TaskRecord;

import java.util.List;

public interface TaskDagService {
    //1.0 task order management
    boolean isOrdered(String orderKey);

    //2.1 task order management
    Long createOrder(TaskOrder taskOrder);
    int updateOrderByKey(String orderKey, TaskOrder taskOrder);
    int deleteOrderByKey(String orderKey, boolean caseCade);

    //2.0 task record management
    List<TaskRecord> getByIds(List<Long> listOfId);
    List<Long> getNextTaskIds(int count);
    List<Long> createTasks(List<TaskRecord> records);
    List<TaskRecord> findTaskByOrderKey(String orderKey);
    int countTaskByOrderKey(String orderKey);

    //3.0 runtime related
    void load(String orderKey);
    void start(String orderKey);
    void loadAndStart(String orderKey);


    boolean isSuccess(String orderKey);

    <I,O> void receiveTaskEvent(Long taskId, TaskStatus newStatus, TaskOutput<O> output);

}
