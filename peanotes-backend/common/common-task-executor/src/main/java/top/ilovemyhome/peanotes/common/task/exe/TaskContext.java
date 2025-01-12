package top.ilovemyhome.peanotes.common.task.exe;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.HandlerStatus;
import top.ilovemyhome.peanotes.common.task.exe.helper.TaskExecutorHelper;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;

public interface TaskContext {


    Path getTaskLogFilePath();

    Path getTaskTempFilePath();

    default String getTaskParam(){
        return null;
    }

    Long getTaskId();

    String getTaskName();
    FileAppender<ILoggingEvent> getLogFileAppender();
    Logger getLogger();

    Long getLogId();

    int getShardIndex();

    int getShardTotal();

    HandlerStatus getHandlerStatus();
    void setHandlerStatus(HandlerStatus status);

    String getHandlerMessage();
    void setHandlerMessage(String message);

    static Builder builder(){
        return new Builder();
    }

    class Builder {
        private TaskExecutor taskExecutor;
        private Long taskId;
        private String taskName;
        private Long logId;
        private String taskParam;
        private int shardIndex;
        private int shardTotal;

        public Builder withTaskExecutor(TaskExecutor taskExecutor){
            this.taskExecutor = taskExecutor;
            return this;
        }
        public Builder withTaskId(Long taskId) {
            this.taskId = taskId;
            return this;
        }
        public Builder withTaskName(String taskName) {
            this.taskName = taskName;
            return this;
        }
        public Builder withLogId(Long logId) {
            this.logId = logId;
            return this;
        }
        public Builder withTaskParam(String taskParam) {
            this.taskParam = taskParam;
            return this;
        }
        public Builder withShardIndex(int shardIndex) {
            this.shardIndex = shardIndex;
            return this;
        }
        public Builder withShardTotal(int shardTotal) {
            this.shardTotal = shardTotal;
            return this;
        }
        public TaskContextImpl build(){
            return new TaskContextImpl(taskExecutor, taskId, taskName, logId, taskParam, shardIndex, shardTotal);
        }
    }
}

class TaskContextImpl implements TaskContext {

    private static final Logger log = LoggerFactory.getLogger(TaskContextImpl.class);
    private final Long taskId;
    private final String taskName;
    private final Long logId;
    private final String taskParam;
    private final Path taskLogFilePath;
    private final Path taskTempFilePath;
    private final int shardIndex;
    private final int shardTotal;
    private volatile HandlerStatus handlerStatus;
    private volatile String handlerMessage;

    private final transient FileAppender<ILoggingEvent> logFileAppender;
    private final transient Logger logger;

    public TaskContextImpl(TaskExecutor taskExecutor
        , Long taskId, String taskName, Long logId
        , String taskParam, int shardTotal, int shardIndex) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.logId = logId;
        this.taskParam = taskParam;
        this.shardTotal = shardTotal;
        this.shardIndex = shardIndex;

        TaskExecutorContext taskExecutorContext = taskExecutor.getContext();
        this.taskLogFilePath = TaskExecutorHelper.taskLogFilePath(
            taskExecutorContext.getLogRootPath()
            , this.taskId
            , this.taskName
            , LocalDate.now()
            , logId
        );
        this.taskTempFilePath = TaskExecutorHelper.taskTaskTempPath(
            taskExecutorContext.getScriptSourcePath()
            , taskName
            , this.logId
        );
        this.logFileAppender = initFileAppender();
        this.logger = initLogger();
    }



    @Override
    public String getTaskParam() {
        return taskParam;
    }

    @Override
    public Long getTaskId() {
        return this.taskId;
    }

    @Override
    public String getTaskName() {
        return this.taskName;
    }

    @Override
    public FileAppender<ILoggingEvent> getLogFileAppender() {
        return logFileAppender;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public Long getLogId() {
        return logId;
    }

    @Override
    public Path getTaskLogFilePath() {
        return taskLogFilePath;
    }

    @Override
    public Path getTaskTempFilePath() {
        return taskTempFilePath;
    }


    @Override
    public int getShardIndex() {
        return shardIndex;
    }

    @Override
    public int getShardTotal() {
        return shardTotal;
    }

    @Override
    public HandlerStatus getHandlerStatus() {
        return this.handlerStatus;
    }

    @Override
    public void setHandlerStatus(HandlerStatus status) {
        this.handlerStatus = status;
    }

    @Override
    public String getHandlerMessage() {
        return this.handlerMessage;
    }

    @Override
    public void setHandlerMessage(String message) {
        this.handlerMessage = message;
    }

    private FileAppender<ILoggingEvent> initFileAppender(){
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
        ple.setContext(lc);
        ple.start();
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setFile(this.taskLogFilePath.toString());
        fileAppender.setEncoder(ple);
        fileAppender.setContext(lc);
        fileAppender.start();
        return fileAppender;
    }

    private Logger initLogger(){
        //Customise the logger
        String uuid = UUID.randomUUID().toString();
        String loggerName = String.format("[%s]-[%s]-[%d]", uuid, taskName, this.logId);
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(loggerName);
        logger.addAppender(logFileAppender);
        return logger;
    }
}
