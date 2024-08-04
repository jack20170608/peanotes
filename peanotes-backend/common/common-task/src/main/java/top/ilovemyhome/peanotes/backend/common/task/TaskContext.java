package top.ilovemyhome.peanotes.backend.common.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.jdbi.v3.core.Jdbi;

import java.util.concurrent.*;

public abstract class TaskContext {

    private final Jdbi jdbi;
    private final ExecutorService threadPool;
    private TaskFactory taskFactory;
    private TaskRecordDao taskRecordDao;
    private SimpleTaskOrderDao taskOrderDao;
    private TaskDagService taskDagService;

    public void setTaskFactory(TaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    public TaskFactory getTaskFactory() {
        return taskFactory;
    }

    public TaskRecordDao getTaskRecordDao() {
        return taskRecordDao;
    }

    public void setTaskRecordDao(TaskRecordDao taskRecordDao) {
        this.taskRecordDao = taskRecordDao;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }


    public TaskDagService getTaskDagService() {
        return taskDagService;
    }

    public void setTaskDagService(TaskDagService taskDagService) {
        this.taskDagService = taskDagService;
    }

    public SimpleTaskOrderDao getTaskOrderDao() {
        return taskOrderDao;
    }

    public void setTaskOrderDao(SimpleTaskOrderDao taskOrderDao) {
        this.taskOrderDao = taskOrderDao;
    }

    public Jdbi getJdbi() {
        return jdbi;
    }

    protected TaskContext(Jdbi jdbi, ExecutorService threadPool) {
        this.jdbi = jdbi;
        this.threadPool = threadPool;
    }

    protected TaskContext(Jdbi jdbi) {
        int nThreads = Runtime.getRuntime().availableProcessors();
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("TaskDagService-%d").build();
        ExecutorService pool = new ThreadPoolExecutor(nThreads, 16, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024)
            , namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        this.jdbi = jdbi;
        this.threadPool = pool;
    }

}
