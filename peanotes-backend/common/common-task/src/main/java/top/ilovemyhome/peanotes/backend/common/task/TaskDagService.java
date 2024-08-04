package top.ilovemyhome.peanotes.backend.common.task;


import java.util.List;

public interface TaskDagService {
    //1.0 task order management
    boolean isOrdered(String orderKey);
    boolean isOrdered(SimpleTaskOrder taskOrder);
    Long createOrder(SimpleTaskOrder taskOrder);
    int updateOrderByKey(String orderKey, SimpleTaskOrder taskOrder);
    int deleteOrderByKey(String orderKey, boolean caseCade);

    //2.0 task record management
    List<TaskRecord> getByIds(List<Long> listOfId);
    List<Long> getNextTaskIds(int count);
    List<Long> createTasks(List<TaskRecord> records);
    List<TaskRecord> findTaskByOrderKey(String orderKey);
    int countTaskByOrderKey(String orderKey);
    int deleteTaskByOrderKey(String orderKey);

    //3.0 runtime related
    void load(SimpleTaskOrder order);

    void start(SimpleTaskOrder order);

    void loadAndStart(String orderKey);
    void loadAndStart(SimpleTaskOrder order);

    boolean isSuccess(SimpleTaskOrder order);

    <I,O> void receiveTaskEvent(Long taskId, TaskStatus newStatus, TaskOutput<O> output);

}
