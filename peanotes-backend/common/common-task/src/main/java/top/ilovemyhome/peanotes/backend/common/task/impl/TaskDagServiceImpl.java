package top.ilovemyhome.peanotes.backend.common.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.task.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TaskDagServiceImpl<I, O> implements TaskDagService<I, O> {

    @Override
    public boolean isOrdered(TaskOrder order) {
        return taskDao.isOrdered(order.getKey());
    }

    @Override
    public boolean isSuccess(TaskOrder order) {
        boolean ordered = taskDao.isOrdered(order.getKey());
        if (!ordered) {
            return false;
        }
        return taskDao.isSuccess(order.getKey());
    }

    @Override
    public List<Task<I, O>> checkStatus(TaskOrder order) {
        boolean ordered = taskDao.isOrdered(order.getKey());
        if (!ordered) {
            throw new IllegalStateException("No tasks for " + order);
        }
        return taskDao.loadTaskForOrder(order.getKey());
    }

    @Override
    public synchronized List<Task<I, O>> order(TaskOrder taskOrder) {
        List<Task<I, O>> taskList;
        if (!taskDao.isOrdered(taskOrder.getKey())) {
            taskList = taskFactory.createTasksForOrder(taskOrder);
            int count = taskDao.createTasksForOrder(taskOrder.getKey(), taskList);
            if (count != taskList.size()) {
                throw new IllegalStateException("Task order failure!");
            }
            LOGGER.info("Task ordered successfully for {}.!", taskOrder);
        } else {
            LOGGER.warn("Task already ordered for {}.", taskOrder);
            throw new IllegalStateException("Task order failure, as already ordered !");
        }
        return taskList;
    }

    @Override
    public synchronized void start(TaskOrder taskOrder) {
        boolean ordered = taskDao.isOrdered(taskOrder.getKey());
        if (!ordered) {
            throw new IllegalStateException("Task start failure, empty task!");
        }
        String orderKey = taskOrder.getKey();
        boolean loaded = taskOrderCache.containsKey(orderKey);
        if (!loaded) {
            loadByOrder(orderKey);
        }
        taskOrderCache.get(orderKey).stream().filter(a -> a.getTaskStatus() == TaskStatus.INIT)
            .filter(Task::isReady)
            .forEach(t -> {
                LOGGER.info("Submit the task {}.", t);
                taskContext.getThreadPool().submit(t);
            });
    }

    @Override
    public synchronized List<Task<I, O>> orderAndStart(TaskOrder taskOrder) {
        List<Task<I, O>> orderedTask = order(taskOrder);
        if (orderedTask.isEmpty()) {
            throw new IllegalStateException("Order failure, empty task!");
        }
        start(taskOrder);
        return taskOrderCache.get(taskOrder.getKey());
    }

    @Override
    public synchronized void receiveTaskEvent(Long taskId, TaskStatus newStatus, TaskOutput<O> output) {
        String orderKey = taskIdOrderCache.getOrDefault(taskId, null);
        if (orderKey == null) {
            loadByTaskId(taskId);
        }
        if (!taskCache.containsKey(taskId) && !taskOrderCache.containsKey(orderKey)) {
            throw new IllegalStateException("Data issue, please check!");
        }
        Task<I, O> targetTask = taskCache.get(taskId);
        if (!(targetTask instanceof AsyncTask)){
            throw new IllegalArgumentException("The target task is not an AsyncTask!");
        }
        TaskStatus oldStatus = targetTask.getTaskStatus();
        if (newStatus == TaskStatus.SUCCESS) {
            if (oldStatus == TaskStatus.RUNNING || oldStatus == TaskStatus.UNKNOWN || oldStatus == TaskStatus.ERROR) {
                targetTask.success(output);

            } else {
                LOGGER.warn("Ignore the event, as the task status " + oldStatus + " not in [running, unknown, error] status.]");
            }
        } else if (newStatus == TaskStatus.ERROR || newStatus == TaskStatus.TIMEOUT || newStatus == TaskStatus.UNKNOWN) {
            targetTask.failure(newStatus, output);
        } else if (newStatus == TaskStatus.INIT) {
            if (oldStatus == TaskStatus.ERROR || oldStatus == TaskStatus.UNKNOWN || oldStatus == TaskStatus.TIMEOUT) {
                taskContext.getThreadPool().submit(targetTask);
            }
        }
    }

    public TaskDagServiceImpl(TaskContext<I, O> taskContext) {
        this.taskContext = taskContext;
        this.taskDao = taskContext.getTaskDao();
        this.taskFactory = taskContext.getTaskFactory();
    }

    private void loadByOrder(String orderKey) {
        LOGGER.info("Loading task {}.", orderKey);
        List<Task<I, O>> taskList = taskDao.loadTaskForOrder(orderKey);
        if (Objects.isNull(taskList) || taskList.isEmpty()) {
            throw new IllegalStateException("Empty task list, please order the task first!");
        }
        taskList.forEach(task -> {
            taskCache.put(task.getId(), task);
            taskIdOrderCache.put(task.getId(), orderKey);
        });
        taskList.forEach(task -> {
            if (Objects.nonNull(task.getSuccessorIds()) && !task.getSuccessorIds().isEmpty()){
                task.getSuccessorIds().forEach(id -> {
                    if (!taskCache.containsKey(id)) {
                        throw new IllegalStateException("Cannot find task with id " + id);
                    }
                    Task<I, O> successorTask = taskCache.get(id);
                    task.addSuccessorTask(successorTask, false);
                });
            }
        });
        taskOrderCache.put(orderKey, taskList);
    }

    private void loadByTaskId(Long taskId) {
        String orderKey = taskDao.getTaskOrderByTaskId(taskId);
        if (Objects.isNull(orderKey)) {
            throw new IllegalStateException("Cannot find the order info for taskId " + taskId);
        }
        loadByOrder(orderKey);
    }

    private final Map<Long, Task<I, O>> taskCache = new ConcurrentHashMap<>();
    private final Map<String, List<Task<I, O>>> taskOrderCache = new ConcurrentHashMap<>();
    private final Map<Long, String> taskIdOrderCache = new ConcurrentHashMap<>();

    private final TaskContext<I, O> taskContext;
    private final TaskDao<I, O> taskDao;
    private final TaskFactory<I, O> taskFactory;

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskDagServiceImpl.class);

}
