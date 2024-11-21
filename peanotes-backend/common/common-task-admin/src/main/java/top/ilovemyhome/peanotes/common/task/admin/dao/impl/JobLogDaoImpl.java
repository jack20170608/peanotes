package top.ilovemyhome.peanotes.common.task.admin.dao.impl;

import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.common.task.admin.application.AppContext;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobLogDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.toLocalDateTime;

public class JobLogDaoImpl extends BaseDaoJdbiImpl<JobLog> implements JobLogDao {

    public JobLogDaoImpl(AppContext appContext) {
        super(TableDescription.builder()
            .withName("t_job_log")
            .withIdField(JobLog.Field.id.name())
            .withFieldColumnMap(JobLog.FIELD_COLUMN_MAP)
            .withIdAutoGenerate(true)
            .build(), appContext.getDataSourceFactory().getJdbi());
    }

    @Override
    protected void registerRowMappers(Jdbi jdbi) {
        jdbi.registerRowMapper(JobLog.class, (rs, ctx) -> JobLog.builder()
            .withId(rs.getLong(JobLog.Field.id.getDbColumn()))
            .withJobGroupId(rs.getLong(JobLog.Field.jobGroupId.getDbColumn()))
            .withJobId(rs.getLong(JobLog.Field.jobId.getDbColumn()))
            .withExecutorAddress(rs.getString(JobLog.Field.executorAddress.getDbColumn()))
            .withExecutorHandler(rs.getString(JobLog.Field.executorHandler.getDbColumn()))
            .withExecutorParam(rs.getString(JobLog.Field.executorParam.getDbColumn()))
            .withExecutorShardingParam(rs.getString(JobLog.Field.executorShardingParam.getDbColumn()))
            .withExecutorFailRetryCount(rs.getInt(JobLog.Field.executorFailRetryCount.getDbColumn()))
            .withTriggerTime(toLocalDateTime(rs.getTimestamp(JobLog.Field.triggerTime.getDbColumn())))
            .withTriggerCode(rs.getInt(JobLog.Field.triggerCode.getDbColumn()))
            .withTriggerMsg(rs.getString(JobLog.Field.triggerMsg.getDbColumn()))
            .withHandleTime(toLocalDateTime(rs.getTimestamp(JobLog.Field.handleTime.getDbColumn())))
            .withHandleCode(rs.getInt(JobLog.Field.handleCode.getDbColumn()))
            .withHandleMsg(rs.getString(JobLog.Field.handleMsg.getDbColumn()))
            .withAlarmStatus(rs.getInt(JobLog.Field.alarmStatus.getDbColumn()))
            .build()
        );
    }

    @Override
    public int deleteByJobId(Long jobId) {
        String sql = "delete from t_job_log where job_id = :jobId";
        return delete(sql, Map.of("jobId", jobId), null);
    }

    //todo
    @Override
    public List<Long> findLostJobIds(LocalDateTime losedTime) {
        return List.of();
    }

    @Override
    public int updateHandleInfo(JobLog xxlJobLog) {
        return 0;
    }

    @Override
    public Map<String, Object> findLogReport(LocalDateTime fromDt, LocalDateTime toDt) {
        return Map.of();
    }

    @Override
    public List<Long> findFailJobLogIds(Long id) {
        return List.of();
    }

    @Override
    public int updateAlarmStatus(long failLogId, int i, int i1) {
        return 0;
    }
}
