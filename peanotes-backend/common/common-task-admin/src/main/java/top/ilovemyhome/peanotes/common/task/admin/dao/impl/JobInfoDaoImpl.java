package top.ilovemyhome.peanotes.common.task.admin.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SearchCriteria;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Page;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Pageable;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.impl.PageRequest;
import top.ilovemyhome.peanotes.common.task.admin.application.AppContext;
import top.ilovemyhome.peanotes.common.task.admin.core.glue.GlueTypeEnum;
import top.ilovemyhome.peanotes.common.task.admin.core.route.ExecutorRouteStrategyEnum;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.ExecutorBlockStrategyEnum;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.MisfireStrategyEnum;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobInfoDao;
import top.ilovemyhome.peanotes.common.task.admin.dao.helper.SqlHelper;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobInfo;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.ScheduleType;
import top.ilovemyhome.peanotes.common.task.admin.web.dto.JobInfoQueryDto;
import top.ilovemyhome.peanotes.common.task.admin.web.dto.helper.DtoHelper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.toLocalDateTime;
import static top.ilovemyhome.peanotes.backend.common.utils.StringConvertUtils.toEnum;

public class JobInfoDaoImpl extends BaseDaoJdbiImpl<JobInfo> implements JobInfoDao {

    public JobInfoDaoImpl(AppContext appContext) {
        super(TableDescription.builder()
                .withName("t_job_info")
                .withIdAutoGenerate(true)
                .withFieldColumnMap(JobInfo.FIELD_COLUMN_MAP)
                .withIdField(JobInfo.Field.id.name())
                .build()
            , appContext.getDataSourceFactory().getJdbi());
    }

    @Override
    protected void registerRowMappers(Jdbi jdbi) {
        jdbi.registerRowMapper(JobInfo.class, (rs, ctx) -> JobInfo.builder()
            .withId(rs.getLong(JobInfo.Field.id.getDbColumn()))
            .withJobGroupId(rs.getLong(JobInfo.Field.jobGroupId.getDbColumn()))
            .withJobDesc(rs.getString(JobInfo.Field.jobdesc.getDbColumn()))
            .withAddTime(toLocalDateTime(rs.getTimestamp(JobInfo.Field.addTime.getDbColumn())))
            .withUpdateTime(toLocalDateTime(rs.getTimestamp(JobInfo.Field.updateTime.getDbColumn())))
            .withAuthor(rs.getString(JobInfo.Field.author.getDbColumn()))
            .withAlarmEmail(rs.getString(JobInfo.Field.alarmEmail.getDbColumn()))
            .withScheduleType(toEnum(ScheduleType.class, rs.getString(JobInfo.Field.scheduleType.getDbColumn())))
            .withMisfireStrategy(toEnum(MisfireStrategyEnum.class, rs.getString(JobInfo.Field.misfireStrategy.getDbColumn())))
            .withExecutorRouteStrategy(toEnum(ExecutorRouteStrategyEnum.class, rs.getString(JobInfo.Field.executorRouteStrategy.getDbColumn())))
            .withExecutorHandler(rs.getString(JobInfo.Field.executorHandler.getDbColumn()))
            .withExecutorParam(rs.getString(JobInfo.Field.executorParam.getDbColumn()))
            .withExecutorBlockStrategy(toEnum(ExecutorBlockStrategyEnum.class, rs.getString(JobInfo.Field.executorBlockStrategy.getDbColumn())))
            .withExecutorTimeout(rs.getInt(JobInfo.Field.executorTimeout.getDbColumn()))
            .withExecutorFailRetryCount(rs.getInt(JobInfo.Field.executorFailRetryCount.getDbColumn()))
            .withGlueType(toEnum(GlueTypeEnum.class, rs.getString(JobInfo.Field.glueType.getDbColumn())))
            .withGlueSource(rs.getString(JobInfo.Field.glueSource.getDbColumn()))
            .withGlueRemark(rs.getString(JobInfo.Field.glueRemark.getDbColumn()))
            .withGlueUpdateTime(toLocalDateTime(rs.getTimestamp(JobInfo.Field.glueUpdateTime.getDbColumn())))
            .withChildJobId(rs.getString(JobInfo.Field.childJobId.getDbColumn()))
            .withTriggerStatus(rs.getInt(JobInfo.Field.triggerStatus.getDbColumn()))
            .withTriggerLastTime(toLocalDateTime(rs.getTimestamp(JobInfo.Field.triggerLastTime.getDbColumn())))
            .withTriggerNextTime(toLocalDateTime(rs.getTimestamp(JobInfo.Field.triggerNextTime.getDbColumn())))
            .build());
    }

    @Override
    public Page<JobInfo> query(JobInfoQueryDto queryDto) {
        SearchCriteria s = new SearchCriteria() {
            @Override
            public Map<String, Object> normalParams() {
                Map<String, Object> paramMap = new HashMap<>(5);
                if (Objects.nonNull(queryDto.jobGroupId())) {
                    paramMap.put("jobGroupId", queryDto.jobGroupId());
                }
                if (queryDto.triggerStatus() > 0) {
                    paramMap.put("triggerStatus", queryDto.triggerStatus());
                }
                if (StringUtils.isNoneBlank(queryDto.jobDesc())) {
                    String jobDesc = queryDto.jobDesc();
                    paramMap.put("jobDesc", SqlHelper.fuzzyString(jobDesc, true, true));
                }
                if (StringUtils.isNoneBlank(queryDto.executorHandler())) {
                    String executorHandler = queryDto.executorHandler();
                    paramMap.put("executorHandler", SqlHelper.fuzzyString(executorHandler, true, true));
                }
                if (StringUtils.isNoneBlank(queryDto.author())) {
                    String author = queryDto.author();
                    paramMap.put("author", SqlHelper.fuzzyString(author, false, true));
                }
                return paramMap;
            }

            @Override
            public String whereClause() {
                StringBuilder b = new StringBuilder(" where 1 = 1");
                if (Objects.nonNull(queryDto.jobGroupId())) {
                    b.append(" and job_group_id = :jobGroupId");
                }
                if (queryDto.triggerStatus() > 0) {
                    b.append(" and trigger_status = :triggerStatus");
                }
                if (StringUtils.isNoneBlank(queryDto.jobDesc())) {
                    b.append(" and job_desc like :jobDesc");
                }
                if (StringUtils.isNoneBlank(queryDto.executorHandler())) {
                    b.append(" and executor_handler like :executorHandler");
                }
                if (StringUtils.isNoneBlank(queryDto.author())) {
                    b.append(" and author like :author");
                }
                return b.toString();
            }
        };
        Pageable pageable;
        if (!Objects.isNull(queryDto.pageRequest())) {
            pageable = DtoHelper.toPageRequest(queryDto.pageRequest());
        } else {
            pageable = new PageRequest(Page.FIRST_PAGE, Page.DEFAULT_PAGE_SIZE);
        }
        return find(s, pageable);
    }

    @Override
    public List<JobInfo> scheduleJobQuery(LocalDateTime maxNextTriggerTime, int recordSize) {
        return find(new SearchCriteria() {
            @Override
            public String whereClause() {
                return """
                    where trigger_status = 1
                    and trigger_next_time <= :nextTriggerTime
                    limit :limit
                    """;
            }

            @Override
            public Map<String, Object> normalParams() {
                return Map.of("nextTriggerTime", maxNextTriggerTime, "limit", recordSize);
            }
        });
    }

    @Override
    public int scheduleUpdate(JobInfo jobInfo) {
        return update("""
            update t_job_info
            set trigger_last_time = :t.triggerLastTime
            , trigger_next_time = :t.triggerNextTime
            , trigger_status = :t.triggerStatus
            where id = :t.id
            """, null, null, Map.of("t", jobInfo));
    }
}
