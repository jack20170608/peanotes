package top.ilovemyhome.peanotes.common.task.exe.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import top.ilovemyhome.peanotes.backend.common.Constants;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.ExecutorBlockStrategyEnum;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

@JsonDeserialize(builder = TriggerParam.Builder.class)
public record TriggerParam(
    Long jobId
    , String executorHandler
    , String executorParams
    , ExecutorBlockStrategyEnum executorBlockStrategy
    , @JsonSerialize(using = DurationSerializer.class)  Duration executorTimeout
    , Long logId
    , @JsonSerialize(using = LocalDateTimeSerializer.class) @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT) LocalDateTime logDateTime
    , TaskType taskType
    , String scriptSource
    , @JsonSerialize(using = LocalDateTimeSerializer.class) @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT) LocalDateTime scriptUpdatetime
    , int broadcastIndex
    , int broadcastTotal) {

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "with")
    public static final class Builder {
        private Long jobId;
        private String executorHandler;
        private String executorParams;
        private ExecutorBlockStrategyEnum executorBlockStrategy;
        private Duration executorTimeout;
        private Long logId;
        private LocalDateTime logDateTime;
        private TaskType taskType;
        private String scriptSource;
        private LocalDateTime scriptUpdatetime;
        private int broadcastIndex;
        private int broadcastTotal;

        private Builder() {
        }



        public Builder withJobId(Long jobId) {
            this.jobId = jobId;
            return this;
        }

        public Builder withExecutorHandler(String executorHandler) {
            this.executorHandler = executorHandler;
            return this;
        }

        public Builder withExecutorParams(String executorParams) {
            this.executorParams = executorParams;
            return this;
        }

        public Builder withExecutorBlockStrategy(ExecutorBlockStrategyEnum executorBlockStrategy) {
            this.executorBlockStrategy = executorBlockStrategy;
            return this;
        }

        @JsonDeserialize(using = DurationDeserializer.class)
        public Builder withExecutorTimeout(Duration executorTimeout) {
            this.executorTimeout = executorTimeout;
            return this;
        }

        public Builder withLogId(Long logId) {
            this.logId = logId;
            return this;
        }

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT)
        public Builder withLogDateTime(LocalDateTime logDateTime) {
            this.logDateTime = logDateTime;
            return this;
        }

        public Builder withTaskType(TaskType taskType) {
            this.taskType = taskType;
            return this;
        }

        public Builder withScriptSource(String scriptSource) {
            this.scriptSource = scriptSource;
            return this;
        }

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT)
        public Builder withScriptUpdatetime(LocalDateTime scriptUpdatetime) {
            this.scriptUpdatetime = scriptUpdatetime;
            return this;
        }

        public Builder withBroadcastIndex(int broadcastIndex) {
            this.broadcastIndex = broadcastIndex;
            return this;
        }

        public Builder withBroadcastTotal(int broadcastTotal) {
            this.broadcastTotal = broadcastTotal;
            return this;
        }

        public TriggerParam build() {
            return new TriggerParam(jobId, executorHandler, executorParams, executorBlockStrategy, executorTimeout, logId, logDateTime, taskType, scriptSource, scriptUpdatetime, broadcastIndex, broadcastTotal);
        }
    }
}
