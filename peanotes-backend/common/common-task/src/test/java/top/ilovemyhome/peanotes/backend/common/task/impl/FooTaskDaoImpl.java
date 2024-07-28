package top.ilovemyhome.peanotes.backend.common.task.impl;

import com.google.common.collect.Maps;
import top.ilovemyhome.peanotes.backend.common.task.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class FooTaskDaoImpl implements TaskDao<String, String> {

    @Override
    public Long getNextId() {
        return ID_SEQ.incrementAndGet();
    }

    @Override
    public List<Task<String, String>> loadTaskForOrder(String orderKey) {
        return DB.get(orderKey);
    }

    @Override
    public int createTasksForOrder(String orderKey, List<Task<String, String>> listOfTask) {
        AtomicLong counter = new AtomicLong();
        DB.put(orderKey, listOfTask);
        listOfTask.forEach(t -> {
            ID_INDEX.put(t.getId(), t);
            ID_ORDER.put(t.getId(), orderKey);
            counter.incrementAndGet();
        });
        return counter.intValue();
    }

    @Override
    public boolean isOrdered(String orderKey) {
        return DB.containsKey(orderKey);
    }

    @Override
    public boolean isSuccess(String orderKey) {
        boolean ordered = isOrdered(orderKey);
        if (!ordered){
            return false;
        }
        boolean success = true;
        for(Task<String, String> t : DB.get(orderKey)){
            if (t.getTaskStatus() != TaskStatus.SUCCESS) {
                success = false;
                break;
            }
        };
        return success;
    }

    @Override
    public int start(Long id, TaskInput<String> input, LocalDateTime startDt) {
        return 1;
    }

    @Override
    public int stop(Long id, TaskStatus newStatus, TaskOutput<String> output, LocalDateTime stopDt) {
        return 1;
    }

    @Override
    public String getTaskOrderByTaskId(Long taskId) {
        return ID_ORDER.get(taskId);
    }

    private final static Map<String, List<Task<String, String>>> DB = Maps.newConcurrentMap();
    private final static Map<Long, Task<String, String>> ID_INDEX = Maps.newConcurrentMap();
    private final static Map<Long, String> ID_ORDER = Maps.newConcurrentMap();
    private final static AtomicLong ID_SEQ = new AtomicLong(0);
}
