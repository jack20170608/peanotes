package top.ilovemyhome.peanotes.backend.common.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.common.task.persistent.TaskOrderDao;
import top.ilovemyhome.peanotes.backend.common.task.persistent.TaskRecordDao;
import top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils;

import java.sql.Types;
import java.time.YearMonth;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

public abstract class TaskContext {

    private final Jdbi jdbi;
    private final ExecutorService threadPool;
    private TaskFactory taskFactory;
    private TaskRecordDao taskRecordDao;
    private TaskOrderDao taskOrderDao;
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

    public TaskOrderDao getTaskOrderDao() {
        return taskOrderDao;
    }

    public void setTaskOrderDao(TaskOrderDao taskOrderDao) {
        this.taskOrderDao = taskOrderDao;
    }

    public Jdbi getJdbi() {
        return jdbi;
    }

    protected TaskContext(Jdbi jdbi) {
        this(jdbi, null);
    }

    protected TaskContext(Jdbi jdbi, ExecutorService threadPool) {
        int nThreads = Runtime.getRuntime().availableProcessors();
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("TaskDagService-%d").build();
        this.threadPool = Objects.requireNonNullElseGet(threadPool, () -> new ThreadPoolExecutor(nThreads, 16, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024)
            , namedThreadFactory, new ThreadPoolExecutor.AbortPolicy()));
        this.jdbi = jdbi;
        //customise some argument mapper
        jdbi.registerArgument(new AbstractArgumentFactory<Map<String, String>>(Types.VARCHAR) {
            @Override
            protected Argument build(Map<String, String> value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, JacksonUtil.toJson(value));
            }
        });
        jdbi.registerArgument(new AbstractArgumentFactory<Set<Long>>(Types.VARCHAR) {
            @Override
            protected Argument build(Set<Long> value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, JacksonUtil.toJson(value));
            }
        });
        jdbi.registerArgument(new AbstractArgumentFactory<YearMonth>(Types.VARCHAR) {
            @Override
            protected Argument build(YearMonth value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, LocalDateUtils.formatYearMonth(value));
            }
        });
        jdbi.registerArgument(new AbstractArgumentFactory<TaskInput<?>>(Types.VARCHAR) {
            @Override
            protected Argument build(TaskInput taskInput, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, JacksonUtil.toJson(taskInput));
            }
        });
    }
}
