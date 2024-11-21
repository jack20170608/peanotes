package top.ilovemyhome.peanotes.common.task.admin.domain;

import com.google.common.collect.ImmutableMap;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * xxl-job log, used to track trigger process
 * @author xuxueli  2015-12-19 23:19:09
 */
public class JobLog {

	private Long id;
	// job info
	private Long jobGroupId;
	private Long jobId;

	// execute info
	private String executorAddress;
	private String executorHandler;
	private String executorParam;
	private String executorShardingParam;
	private int executorFailRetryCount;

	// trigger info
	private LocalDateTime triggerTime;
	private int triggerCode;
	private String triggerMsg;

	// handle info
	private LocalDateTime handleTime;
	private int handleCode;
	private String handleMsg;

	// alarm info
	private int alarmStatus;

    public enum Field {
        id("ID", true)
        , jobGroupId("JOB_GROUP_ID" )
        , jobId("JOB_ID" )
        , executorAddress("EXECUTOR_ADDRESS" )
        , executorHandler("EXECUTOR_HANDLER" )
        , executorParam("EXECUTOR_PARAM" )
        , executorShardingParam("EXECUTOR_SHARDING_PARAM" )
        , executorFailRetryCount("EXECUTOR_FAIL_RETRY_COUNT" )
        , triggerTime("TRIGGER_TIME" )
        , triggerCode("TRIGGER_CODE" )
        , triggerMsg("TRIGGER_MSG" )
        , handleTime("HANDLE_TIME" )
        , handleCode("HANDLE_CODE" )
        , handleMsg("HANDLE_MSG" )
        , alarmStatus("ALARM_STATUS" );

        private final String dbColumn;
        private final boolean isId;

        Field(String dbColumn) {
            this.dbColumn = dbColumn;
            this.isId = false;
        }

        Field(String dbColumn, boolean isId) {
            this.dbColumn = dbColumn;
            this.isId = isId;
        }

        public String getDbColumn() {
            return dbColumn;
        }

        public boolean isId() {
            return isId;
        }
    }

    public static final Map<String, String> FIELD_COLUMN_MAP = ImmutableMap.copyOf(Stream.of(JobLog.Field.values())
        .collect(Collectors.toMap(JobLog.Field::name, JobLog.Field::getDbColumn)));

    public Long getId() {
        return id;
    }

    public Long getJobGroupId() {
        return jobGroupId;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getExecutorAddress() {
        return executorAddress;
    }

    public String getExecutorHandler() {
        return executorHandler;
    }

    public String getExecutorParam() {
        return executorParam;
    }

    public String getExecutorShardingParam() {
        return executorShardingParam;
    }

    public int getExecutorFailRetryCount() {
        return executorFailRetryCount;
    }

    public LocalDateTime getTriggerTime() {
        return triggerTime;
    }

    public int getTriggerCode() {
        return triggerCode;
    }

    public String getTriggerMsg() {
        return triggerMsg;
    }

    public LocalDateTime getHandleTime() {
        return handleTime;
    }

    public int getHandleCode() {
        return handleCode;
    }

    public String getHandleMsg() {
        return handleMsg;
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setJobGroupId(Long jobGroupId) {
        this.jobGroupId = jobGroupId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public void setExecutorAddress(String executorAddress) {
        this.executorAddress = executorAddress;
    }

    public void setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
    }

    public void setExecutorParam(String executorParam) {
        this.executorParam = executorParam;
    }

    public void setExecutorShardingParam(String executorShardingParam) {
        this.executorShardingParam = executorShardingParam;
    }

    public void setExecutorFailRetryCount(int executorFailRetryCount) {
        this.executorFailRetryCount = executorFailRetryCount;
    }

    public void setTriggerTime(LocalDateTime triggerTime) {
        this.triggerTime = triggerTime;
    }

    public void setTriggerCode(int triggerCode) {
        this.triggerCode = triggerCode;
    }

    public void setTriggerMsg(String triggerMsg) {
        this.triggerMsg = triggerMsg;
    }

    public void setHandleTime(LocalDateTime handleTime) {
        this.handleTime = handleTime;
    }

    public void setHandleCode(int handleCode) {
        this.handleCode = handleCode;
    }

    public void setHandleMsg(String handleMsg) {
        this.handleMsg = handleMsg;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        // job info
        private Long jobGroupId;
        private Long jobId;
        // execute info
        private String executorAddress;
        private String executorHandler;
        private String executorParam;
        private String executorShardingParam;
        private int executorFailRetryCount;
        // trigger info
        private LocalDateTime triggerTime;
        private int triggerCode;
        private String triggerMsg;
        // handle info
        private LocalDateTime handleTime;
        private int handleCode;
        private String handleMsg;
        // alarm info
        private int alarmStatus;

        private Builder() {
        }



        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withJobGroupId(Long jobGroupId) {
            this.jobGroupId = jobGroupId;
            return this;
        }

        public Builder withJobId(Long jobId) {
            this.jobId = jobId;
            return this;
        }

        public Builder withExecutorAddress(String executorAddress) {
            this.executorAddress = executorAddress;
            return this;
        }

        public Builder withExecutorHandler(String executorHandler) {
            this.executorHandler = executorHandler;
            return this;
        }

        public Builder withExecutorParam(String executorParam) {
            this.executorParam = executorParam;
            return this;
        }

        public Builder withExecutorShardingParam(String executorShardingParam) {
            this.executorShardingParam = executorShardingParam;
            return this;
        }

        public Builder withExecutorFailRetryCount(int executorFailRetryCount) {
            this.executorFailRetryCount = executorFailRetryCount;
            return this;
        }

        public Builder withTriggerTime(LocalDateTime triggerTime) {
            this.triggerTime = triggerTime;
            return this;
        }

        public Builder withTriggerCode(int triggerCode) {
            this.triggerCode = triggerCode;
            return this;
        }

        public Builder withTriggerMsg(String triggerMsg) {
            this.triggerMsg = triggerMsg;
            return this;
        }

        public Builder withHandleTime(LocalDateTime handleTime) {
            this.handleTime = handleTime;
            return this;
        }

        public Builder withHandleCode(int handleCode) {
            this.handleCode = handleCode;
            return this;
        }

        public Builder withHandleMsg(String handleMsg) {
            this.handleMsg = handleMsg;
            return this;
        }

        public Builder withAlarmStatus(int alarmStatus) {
            this.alarmStatus = alarmStatus;
            return this;
        }

        public JobLog build() {
            JobLog jobLog = new JobLog();
            jobLog.triggerMsg = this.triggerMsg;
            jobLog.jobId = this.jobId;
            jobLog.executorFailRetryCount = this.executorFailRetryCount;
            jobLog.triggerTime = this.triggerTime;
            jobLog.handleMsg = this.handleMsg;
            jobLog.alarmStatus = this.alarmStatus;
            jobLog.executorShardingParam = this.executorShardingParam;
            jobLog.jobGroupId = this.jobGroupId;
            jobLog.executorParam = this.executorParam;
            jobLog.executorAddress = this.executorAddress;
            jobLog.handleCode = this.handleCode;
            jobLog.triggerCode = this.triggerCode;
            jobLog.executorHandler = this.executorHandler;
            jobLog.id = this.id;
            jobLog.handleTime = this.handleTime;
            return jobLog;
        }
    }
}
