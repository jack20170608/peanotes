package top.ilovemyhome.peanotes.common.task.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.collect.ImmutableMap;
import top.ilovemyhome.peanotes.backend.common.Constants;
import top.ilovemyhome.peanotes.common.task.admin.core.glue.GlueTypeEnum;
import top.ilovemyhome.peanotes.common.task.admin.core.route.ExecutorRouteStrategyEnum;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.ExecutorBlockStrategyEnum;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.MisfireStrategyEnum;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.ScheduleType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonDeserialize(builder = JobInfo.Builder.class)
public class JobInfo {

    private JobInfo() {
    }

    private Long id;                // 主键ID
    private Long jobGroupId;        // 执行器主键ID
    private String jobDesc;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = Constants.JSON_DATETIME_FORMAT)
    private LocalDateTime addTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = Constants.JSON_DATETIME_FORMAT)
    private LocalDateTime updateTime;

    private String author;        // 负责人
    private String alarmEmail;    // 报警邮件

    private ScheduleType scheduleType;            // 调度类型
    private String scheduleConf;            // 调度配置，值含义取决于调度类型
    private MisfireStrategyEnum misfireStrategy;            // 调度过期策略

    private ExecutorRouteStrategyEnum executorRouteStrategy;    // 执行器路由策略
    private String executorHandler;            // 执行器，任务Handler名称
    private String executorParam;            // 执行器，任务参数
    private ExecutorBlockStrategyEnum executorBlockStrategy;    // 阻塞处理策略
    private int executorTimeout;            // 任务执行超时时间，单位秒
    private int executorFailRetryCount;        // 失败重试次数

    private GlueTypeEnum glueType;        // GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
    private String glueSource;        // GLUE源代码
    private String glueRemark;        // GLUE备注

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = Constants.JSON_DATETIME_FORMAT)
    private LocalDateTime glueUpdateTime;    // GLUE更新时间

    private String childJobId;        // 子任务ID，多个逗号分隔
    private int triggerStatus;        // 调度状态：0-停止，1-运行
    private LocalDateTime triggerLastTime;    // 上次调度时间
    private LocalDateTime triggerNextTime;    // 下次调度时间



    public enum Field {
        id("ID", true)
        , jobGroupId("JOB_GROUP_ID" )
        , jobdesc("JOB_DESC" )
        , addTime("ADD_TIME")
        , updateTime("UPDATE_TIME")
        , author("AUTHOR")
        , alarmEmail("ALARM_EMAIL")
        , scheduleType("SCHEDULE_TYPE")
        , misfireStrategy("MISFIRE_STRATEGY")
        , executorRouteStrategy("EXECUTOR_ROUTE_STRATEGY")
        , executorHandler("EXECUTOR_HANDLER")
        , executorParam("EXECUTOR_PARAM")
        , executorBlockStrategy("EXECUTOR_BLOCK_STRATEGY")
        , executorTimeout("EXECUTOR_TIMEOUT")
        , executorFailRetryCount("EXECUTOR_FAIL_RETRY_COUNT")
        , glueType("GLUE_TYPE")
        , glueSource("GLUE_SOURCE")
        , glueRemark("GLUE_REMARK")
        , glueUpdateTime("GLUE_UPDATE_TIME")
        , childJobId("CHILD_JOB_ID")
        , triggerStatus("TRIGGER_STATUS")
        , triggerLastTime("TRIGGER_LAST_TIME")
        , triggerNextTime("TRIGGER_NEXT_TIME");

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

    public static final Map<String, String> FIELD_COLUMN_MAP = ImmutableMap.copyOf(Stream.of(JobInfo.Field.values())
        .collect(Collectors.toMap(JobInfo.Field::name, JobInfo.Field::getDbColumn)));

    //The following field will changed in the runtime
    public void setChildJobId(String childJobId) {
        this.childJobId = childJobId;
    }

    public void setExecutorParam(String executorParam) {
        this.executorParam = executorParam;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public void setTriggerStatus(int triggerStatus) {
        this.triggerStatus = triggerStatus;
    }

    public void setTriggerLastTime(LocalDateTime triggerLastTime) {
        this.triggerLastTime = triggerLastTime;
    }

    public void setTriggerNextTime(LocalDateTime triggerNextTime) {
        this.triggerNextTime = triggerNextTime;
    }

    public void setGlueUpdateTime(LocalDateTime glueUpdateTime) {
        this.glueUpdateTime = glueUpdateTime;
    }

    public void setGlueSource(String glueSource) {
        this.glueSource = glueSource;
    }

    public Long getId() {
        return id;
    }

    public Long getJobGroupId() {
        return jobGroupId;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public String getAuthor() {
        return author;
    }

    public String getAlarmEmail() {
        return alarmEmail;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public String getScheduleConf() {
        return scheduleConf;
    }

    public MisfireStrategyEnum getMisfireStrategy() {
        return misfireStrategy;
    }

    public ExecutorRouteStrategyEnum getExecutorRouteStrategy() {
        return executorRouteStrategy;
    }

    public String getExecutorHandler() {
        return executorHandler;
    }

    public String getExecutorParam() {
        return executorParam;
    }

    public ExecutorBlockStrategyEnum getExecutorBlockStrategy() {
        return executorBlockStrategy;
    }

    public int getExecutorTimeout() {
        return executorTimeout;
    }

    public int getExecutorFailRetryCount() {
        return executorFailRetryCount;
    }

    public GlueTypeEnum getGlueType() {
        return glueType;
    }

    public String getGlueSource() {
        return glueSource;
    }

    public String getGlueRemark() {
        return glueRemark;
    }

    public LocalDateTime getGlueUpdateTime() {
        return glueUpdateTime;
    }

    public String getChildJobId() {
        return childJobId;
    }

    public int getTriggerStatus() {
        return triggerStatus;
    }

    public LocalDateTime getTriggerLastTime() {
        return triggerLastTime;
    }

    public LocalDateTime getTriggerNextTime() {
        return triggerNextTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(JobInfo old){
        return builder()
            .withId(old.id)
            .withJobGroupId(old.jobGroupId)
            .withJobDesc(old.jobDesc)
            .withAddTime(old.addTime)
            .withUpdateTime(old.updateTime)
            .withAuthor(old.author)
            .withAlarmEmail(old.alarmEmail)
            .withScheduleType(old.scheduleType)
            .withScheduleConf(old.scheduleConf)
            .withMisfireStrategy(old.misfireStrategy)
            .withExecutorRouteStrategy(old.executorRouteStrategy)
            .withExecutorHandler(old.executorHandler)
            .withExecutorParam(old.executorParam)
            .withExecutorBlockStrategy(old.executorBlockStrategy)
            .withExecutorTimeout(old.executorTimeout)
            .withExecutorFailRetryCount(old.executorFailRetryCount)
            .withGlueType(old.glueType)
            .withGlueSource(old.glueSource)
            .withGlueRemark(old.glueRemark)
            .withGlueUpdateTime(old.glueUpdateTime)
            .withChildJobId(old.childJobId)
            .withTriggerStatus(old.triggerStatus)
            .withTriggerLastTime(old.triggerLastTime)
            .withTriggerNextTime(old.triggerNextTime)
            ;
    }

    @JsonPOJOBuilder(withPrefix = "with")
    public static final class Builder {
        private Long id;                // 主键ID
        private Long jobGroupId;        // 执行器主键ID
        private String jobDesc;
        private LocalDateTime addTime;
        private LocalDateTime updateTime;
        private String author;        // 负责人
        private String alarmEmail;    // 报警邮件
        private ScheduleType scheduleType;            // 调度类型
        private String scheduleConf;            // 调度配置，值含义取决于调度类型
        private MisfireStrategyEnum misfireStrategy;            // 调度过期策略
        private ExecutorRouteStrategyEnum executorRouteStrategy;    // 执行器路由策略
        private String executorHandler;            // 执行器，任务Handler名称
        private String executorParam;            // 执行器，任务参数
        private ExecutorBlockStrategyEnum executorBlockStrategy;    // 阻塞处理策略
        private int executorTimeout;            // 任务执行超时时间，单位秒
        private int executorFailRetryCount;        // 失败重试次数
        private GlueTypeEnum glueType;        // GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
        private String glueSource;        // GLUE源代码
        private String glueRemark;        // GLUE备注
        private LocalDateTime glueUpdatetime;    // GLUE更新时间
        private String childJobId;        // 子任务ID，多个逗号分隔
        private int triggerStatus;        // 调度状态：0-停止，1-运行
        private LocalDateTime triggerLastTime;    // 上次调度时间
        private LocalDateTime triggerNextTime;    // 下次调度时间

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

        public Builder withJobDesc(String jobDesc) {
            this.jobDesc = jobDesc;
            return this;
        }

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT)
        public Builder withAddTime(LocalDateTime addTime) {
            this.addTime = addTime;
            return this;
        }

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT)
        public Builder withUpdateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Builder withAuthor(String author) {
            this.author = author;
            return this;
        }

        public Builder withAlarmEmail(String alarmEmail) {
            this.alarmEmail = alarmEmail;
            return this;
        }

        public Builder withScheduleType(ScheduleType scheduleType) {
            this.scheduleType = scheduleType;
            return this;
        }

        public Builder withScheduleConf(String scheduleConf) {
            this.scheduleConf = scheduleConf;
            return this;
        }

        public Builder withMisfireStrategy(MisfireStrategyEnum misfireStrategy) {
            this.misfireStrategy = misfireStrategy;
            return this;
        }

        public Builder withExecutorRouteStrategy(ExecutorRouteStrategyEnum executorRouteStrategy) {
            this.executorRouteStrategy = executorRouteStrategy;
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

        public Builder withExecutorBlockStrategy(ExecutorBlockStrategyEnum executorBlockStrategy) {
            this.executorBlockStrategy = executorBlockStrategy;
            return this;
        }

        public Builder withExecutorTimeout(int executorTimeout) {
            this.executorTimeout = executorTimeout;
            return this;
        }

        public Builder withExecutorFailRetryCount(int executorFailRetryCount) {
            this.executorFailRetryCount = executorFailRetryCount;
            return this;
        }

        public Builder withGlueType(GlueTypeEnum glueType) {
            this.glueType = glueType;
            return this;
        }

        public Builder withGlueSource(String glueSource) {
            this.glueSource = glueSource;
            return this;
        }

        public Builder withGlueRemark(String glueRemark) {
            this.glueRemark = glueRemark;
            return this;
        }

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT)
        public Builder withGlueUpdateTime(LocalDateTime glueUpdateTime) {
            this.glueUpdatetime = glueUpdateTime;
            return this;
        }

        public Builder withChildJobId(String childJobId) {
            this.childJobId = childJobId;
            return this;
        }

        public Builder withTriggerStatus(int triggerStatus) {
            this.triggerStatus = triggerStatus;
            return this;
        }

        public Builder withTriggerLastTime(LocalDateTime triggerLastTime) {
            this.triggerLastTime = triggerLastTime;
            return this;
        }

        public Builder withTriggerNextTime(LocalDateTime triggerNextTime) {
            this.triggerNextTime = triggerNextTime;
            return this;
        }

        public JobInfo build() {
            JobInfo jobInfo = new JobInfo();
            jobInfo.alarmEmail = this.alarmEmail;
            jobInfo.glueType = this.glueType;
            jobInfo.executorBlockStrategy = this.executorBlockStrategy;
            jobInfo.glueSource = this.glueSource;
            jobInfo.updateTime = this.updateTime;
            jobInfo.executorRouteStrategy = this.executorRouteStrategy;
            jobInfo.jobGroupId = this.jobGroupId;
            jobInfo.executorParam = this.executorParam;
            jobInfo.executorFailRetryCount = this.executorFailRetryCount;
            jobInfo.executorHandler = this.executorHandler;
            jobInfo.scheduleConf = this.scheduleConf;
            jobInfo.childJobId = this.childJobId;
            jobInfo.addTime = this.addTime;
            jobInfo.glueRemark = this.glueRemark;
            jobInfo.scheduleType = this.scheduleType;
            jobInfo.author = this.author;
            jobInfo.triggerLastTime = this.triggerLastTime;
            jobInfo.triggerStatus = this.triggerStatus;
            jobInfo.glueUpdateTime = this.glueUpdatetime;
            jobInfo.misfireStrategy = this.misfireStrategy;
            jobInfo.executorTimeout = this.executorTimeout;
            jobInfo.id = this.id;
            jobInfo.triggerNextTime = this.triggerNextTime;
            jobInfo.jobDesc = this.jobDesc;
            return jobInfo;
        }
    }
}
