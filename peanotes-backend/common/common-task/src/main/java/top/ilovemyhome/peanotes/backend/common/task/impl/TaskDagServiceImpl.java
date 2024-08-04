package top.ilovemyhome.peanotes.backend.common.task.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SearchCriteria;
import top.ilovemyhome.peanotes.backend.common.task.*;
import top.ilovemyhome.peanotes.backend.common.task.dag.DagHelper;
import top.ilovemyhome.peanotes.backend.common.task.dag.DagNode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TaskDagServiceImpl implements TaskDagService {

    @Override
    public boolean isOrdered(String orderKey) {
        return taskOrderDao.findByKey(orderKey).isPresent();
    }

    @Override
    public boolean isOrdered(SimpleTaskOrder order) {
        return taskOrderDao.findByKey(order.getKey()).isPresent();
    }

    @Override
    public synchronized Long createOrder(SimpleTaskOrder taskOrder) {
        String orderKey = taskOrder.getKey();
        Optional<SimpleTaskOrder> taskOrderOptional = taskOrderDao.findByKey(orderKey);
        Long id = 0L;
        if (taskOrderOptional.isEmpty()) {
            id = taskOrderDao.create(taskOrder);
            taskOrder.setId(id);
        } else {
            throw new IllegalArgumentException("The task order with key: " + orderKey + " already exists");
        }
        return id;
    }

    @Override
    public synchronized int updateOrderByKey(String orderKey, SimpleTaskOrder taskOrder) {
        Optional<SimpleTaskOrder> taskOrderOptional = taskOrderDao.findByKey(orderKey);
        int result;
        if (taskOrderOptional.isPresent()) {
            result = taskOrderDao.updateByKey(orderKey, taskOrder);
        } else {
            throw new IllegalArgumentException("The task order with key: " + orderKey + " not exists");
        }
        return result;
    }

    @Override
    public synchronized int deleteOrderByKey(String orderKey, boolean caseCade) {
        final AtomicInteger result = new AtomicInteger(0);
        int count = countTaskByOrderKey(orderKey);
        if (count > 0) {
            if (caseCade) {
                jdbi.useTransaction(h -> {
                    taskRecordDao.deleteByOrderKey(orderKey);
                    result.set(taskOrderDao.deleteByKey(orderKey));
                });
            } else {
                throw new IllegalArgumentException("The task order with key: " + orderKey + " have tasks linked to it.");
            }
        } else {
            result.set(taskOrderDao.deleteByKey(orderKey));
        }
        return result.get();
    }

    @Override
    public List<Long> getNextTaskIds(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("The count must be greater than 0");
        }
        List<Long> ids = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ids.add(taskRecordDao.getNextId());
        }
        return ImmutableList.copyOf(ids);
    }

    @Override
    public synchronized List<Long> createTasks(List<TaskRecord> records) {
        Objects.requireNonNull(records);
        if (records.isEmpty()) {
            throw new IllegalArgumentException("The records list must not be empty");
        }
        //check if the task order exists or not
        String orderKey = records.getFirst().getOrderKey();
        if (StringUtils.isBlank(orderKey)) {
            throw new IllegalArgumentException("The record order key must not be empty");
        }
        records.forEach(r -> {
            if (!StringUtils.equals(orderKey, r.getOrderKey())) {
                throw new IllegalArgumentException("All records must have the same order key");
            }
        });
        return jdbi.inTransaction(h -> {
            Optional<SimpleTaskOrder> taskOrderOptional = taskOrderDao.findByKey(orderKey);
            final List<Long> rs = new ArrayList<>();
            if (taskOrderOptional.isPresent()) {
                Map<Long, TaskRecord> rMap = findTaskByOrderKey(orderKey).stream().collect(Collectors.toMap(TaskRecord::getId, Function.identity()));
                records.forEach(n -> {
                    if (rMap.containsKey(n.getId())) {
                        throw new IllegalArgumentException("Already existing task record with id: " + n.getId());
                    }
                });

                List<DagNode> dagNodes;
                if (!rMap.isEmpty()) {
                    //check if the DAG have cycle
                    dagNodes = rMap.values().stream().map(this::toDagNode).collect(Collectors.toList());
                } else {
                    dagNodes = new ArrayList<>();
                }
                List<String> taskPath = new ArrayList<>();
                LOGGER.info("================================================");
                DagHelper.visitDAG(dagNodes, taskPath);
                LOGGER.info("{}", taskPath);
                //Add more
                records.forEach(newRecord -> {
                    dagNodes.add(toDagNode(newRecord));
                });
                LOGGER.info("================================================");
                taskPath.clear();
                DagHelper.visitDAG(dagNodes, taskPath);
                LOGGER.info("{}", taskPath);
                //Create
                records.forEach(newRecord -> {
                    rs.add(taskRecordDao.create(newRecord));
                });
            } else {
                throw new IllegalArgumentException("Cannot find task order with key: " + orderKey);
            }
            return rs;
        });
    }

    @Override
    public List<TaskRecord> findTaskByOrderKey(String orderKey) {
        SearchCriteria searchCriteria = TaskSearchCriteria.builder()
            .withOrderKey(orderKey)
            .build();
        return taskRecordDao.find(searchCriteria);
    }

    @Override
    public int countTaskByOrderKey(String orderKey) {
        SearchCriteria searchCriteria = TaskSearchCriteria.builder()
            .withOrderKey(orderKey)
            .build();
        return taskRecordDao.count(searchCriteria);
    }

    @Override
    public int deleteTaskByOrderKey(String orderKey) {
        return taskRecordDao.deleteByOrderKey(orderKey);
    }

    @Override
    public List<TaskRecord> getByIds(List<Long> listOfId) {
        return find(TaskSearchCriteria.builder()
            .withListOfId(listOfId)
            .build());
    }


    private List<TaskRecord> find(TaskSearchCriteria criteria) {
        LOGGER.info("Find task with criteria: {}.", criteria);
        return taskRecordDao.find(criteria);
    }


    @Override
    public boolean isSuccess(SimpleTaskOrder order) {
        boolean ordered = taskRecordDao.isOrdered(order.getKey());
        if (!ordered) {
            return false;
        }
        return taskRecordDao.isSuccess(order.getKey());
    }

    @Override
    public synchronized void load(SimpleTaskOrder taskOrder) {
        Objects.requireNonNull(taskOrder);
        String orderKey = taskOrder.getKey();
        if (StringUtils.isBlank(orderKey)) {
            throw new IllegalArgumentException("Task order key is empty");
        }
        if (!isOrdered(taskOrder)) {
            throw new IllegalStateException("Task not ordered for " + taskOrder);
        }
        loadByOrderKey(orderKey);
    }

    @Override
    public synchronized void start(SimpleTaskOrder taskOrder) {
        Objects.requireNonNull(taskOrder);
        String orderKey = taskOrder.getKey();
        boolean loaded = taskOrderCache.containsKey(orderKey);
        if (!loaded) {
            load(taskOrder);
        }
        ((List<Task>) taskOrderCache.get(orderKey)).stream().filter(a -> a.getTaskStatus() == TaskStatus.INIT)
            .filter(Task::isReady)
            .forEach(t -> {
                LOGGER.info("Submit the task {}.", t);
                taskContext.getThreadPool().submit(t);
            });
    }

    @Override
    public synchronized void loadAndStart(String orderKey){
        Optional<SimpleTaskOrder> order = taskOrderDao.findByKey(orderKey);
        order.ifPresentOrElse(this::loadAndStart, () -> {
            throw new IllegalArgumentException("Cannot find task order with key: " + orderKey);
        });
    }
    @Override
    public synchronized void loadAndStart(SimpleTaskOrder taskOrder) {
        load(taskOrder);
        start(taskOrder);
    }

    @Override
    public synchronized <I, O> void receiveTaskEvent(Long taskId, TaskStatus newStatus, TaskOutput<O> output) {
        String orderKey = (String) taskIdOrderCache.getOrDefault(taskId, null);
            if (orderKey == null) {
            loadByTaskId(taskId);
        }
        if (!taskCache.containsKey(taskId) && !taskOrderCache.containsKey(orderKey)) {
            throw new IllegalStateException("Data issue, please check!");
        }
        Task<I, O> targetTask = (Task<I, O>) taskCache.get(taskId);
        if (!(targetTask instanceof AsyncTask)) {
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

    public TaskDagServiceImpl(TaskContext taskContext) {
        this.taskContext = taskContext;
        this.taskOrderDao = taskContext.getTaskOrderDao();
        this.taskRecordDao = taskContext.getTaskRecordDao();
        this.jdbi = taskContext.getJdbi();
        taskContext.setTaskDagService(this);
    }

    private <I, O> void loadByOrderKey(String orderKey) {
        LOGGER.info("Loading task {}.", orderKey);
        List<Task<I, O>> taskList = taskRecordDao.loadTaskForOrder(orderKey);
        if (Objects.isNull(taskList) || taskList.isEmpty()) {
            throw new IllegalStateException("Empty task list, please add task for this order!");
        }
        taskList.forEach(task -> {
            taskCache.put(task.getId(), task);
            taskIdOrderCache.put(task.getId(), orderKey);
        });
        taskList.forEach(task -> {
            if (Objects.nonNull(task.getSuccessorIds()) && !task.getSuccessorIds().isEmpty()) {
                task.getSuccessorIds().forEach(id -> {
                    if (!taskCache.containsKey(id)) {
                        throw new IllegalStateException("Cannot find task with id " + id);
                    }
                    Task<I, O> successorTask = (Task<I, O>) taskCache.get(id);
                    task.addSuccessorTask(successorTask, false);
                });
            }
        });
        if (taskList.isEmpty()) {
            throw new IllegalStateException("Empty task list!!");
        }
        taskOrderCache.put(orderKey, taskList);
        LOGGER.info("Task ordered successfully!");
    }

    private DagNode toDagNode(TaskRecord record) {
        Objects.requireNonNull(record);
        return new DagNode(record.getId(), record.getName()
            , Objects.nonNull(record.getSuccessorIds()) ? Sets.newHashSet(record.getSuccessorIds()) : null);
    }

    private void loadByTaskId(Long taskId) {
        String orderKey = taskContext.getTaskRecordDao().getTaskOrderByTaskId(taskId);
        if (Objects.isNull(orderKey)) {
            throw new IllegalStateException("Cannot find the order info for taskId " + taskId);
        }
        loadByOrderKey(orderKey);
    }

    private final Map taskCache = new ConcurrentHashMap<>();
    private final Map taskOrderCache = new ConcurrentHashMap<>();
    private final Map taskIdOrderCache = new ConcurrentHashMap<>();

    private final TaskContext taskContext;

    private final TaskRecordDao taskRecordDao;
    private final SimpleTaskOrderDao taskOrderDao;
    private final Jdbi jdbi;

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskDagServiceImpl.class);

}
