package top.ilovemyhome.peanotes.common.task.exe;

import top.ilovemyhome.peanotes.common.task.exe.domain.enums.HandlerStatus;
import top.ilovemyhome.peanotes.common.task.exe.helper.TaskExecutorHelper;

import java.nio.file.Path;
import java.time.LocalDate;

public interface TaskContext {

    Path getTaskLogFilePath();

    Path getTaskTempFilePath();

    default String getTaskParam(){
        return null;
    }

    Long getTaskId();

    String getTaskName();

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
}
