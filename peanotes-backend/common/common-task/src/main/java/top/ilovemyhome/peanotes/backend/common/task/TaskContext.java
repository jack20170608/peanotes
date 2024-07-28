package top.ilovemyhome.peanotes.backend.common.task;

import java.util.concurrent.ExecutorService;

public class TaskContext<I, O> {

    private TaskFactory<I, O> taskFactory;

    private final TaskDao taskDao;
    private final ExecutorService threadPool;

    public void setTaskFactory(TaskFactory<I, O> taskFactory) {
        this.taskFactory = taskFactory;
    }

    public TaskFactory<I, O> getTaskFactory() {
        return taskFactory;
    }

    public TaskDao getTaskDao() {
        return taskDao;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public TaskContext(TaskDao taskDao, ExecutorService threadPool) {
        this.taskDao = taskDao;
        this.threadPool = threadPool;
    }
}
