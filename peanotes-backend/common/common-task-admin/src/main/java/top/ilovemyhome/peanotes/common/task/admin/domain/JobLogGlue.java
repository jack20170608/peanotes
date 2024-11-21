package top.ilovemyhome.peanotes.common.task.admin.domain;


import com.google.common.collect.ImmutableMap;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * xxl-job log for glue, used to track job code process
 *
 * @author xuxueli 2016-5-19 17:57:46
 */
public class JobLogGlue {

    private Long id;
    private Long jobId;                // 任务主键ID
    private String glueType;        // GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
    private String glueSource;
    private String glueRemark;
    private LocalDateTime addDt;
    private LocalDateTime lastUpdateDt;

    public enum Field {
        id("ID", true),
        jobId("JOB_ID"),
        glueType("GLUE_TYPE"),
        glueSource("GLUE_SOURCE"),
        glueRemark("GLUE_REMARK"),
        addDt("ADD_DT", true),
        lastUpdateDt("LAST_UPDATE_DT", true);

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

    public static final Map<String, String> FIELD_COLUMN_MAP = ImmutableMap.copyOf(Stream.of(JobLogGlue.Field.values())
        .collect(Collectors.toMap(JobLogGlue.Field::name, JobLogGlue.Field::getDbColumn)));

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getGlueType() {
        return glueType;
    }

    public String getGlueSource() {
        return glueSource;
    }

    public String getGlueRemark() {
        return glueRemark;
    }

    public LocalDateTime getAddDt() {
        return addDt;
    }

    public LocalDateTime getLastUpdateDt() {
        return lastUpdateDt;
    }

    public static final class Builder {
        private LocalDateTime lastUpdateDt;
        private LocalDateTime addDt;
        private String glueRemark;
        private String glueSource;
        private String glueType;        // GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
        private Long jobId;                // 任务主键ID
        private Long id;

        private Builder() {
        }


        public Builder withLastUpdateDt(LocalDateTime lastUpdateDt) {
            this.lastUpdateDt = lastUpdateDt;
            return this;
        }

        public Builder withAddDt(LocalDateTime addDt) {
            this.addDt = addDt;
            return this;
        }

        public Builder withGlueRemark(String glueRemark) {
            this.glueRemark = glueRemark;
            return this;
        }

        public Builder withGlueSource(String glueSource) {
            this.glueSource = glueSource;
            return this;
        }

        public Builder withGlueType(String glueType) {
            this.glueType = glueType;
            return this;
        }

        public Builder withJobId(Long jobId) {
            this.jobId = jobId;
            return this;
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public JobLogGlue build() {
            JobLogGlue jobLogGlue = new JobLogGlue();
            jobLogGlue.lastUpdateDt = this.lastUpdateDt;
            jobLogGlue.glueSource = this.glueSource;
            jobLogGlue.glueType = this.glueType;
            jobLogGlue.id = this.id;
            jobLogGlue.addDt = this.addDt;
            jobLogGlue.jobId = this.jobId;
            jobLogGlue.glueRemark = this.glueRemark;
            return jobLogGlue;
        }
    }
}
