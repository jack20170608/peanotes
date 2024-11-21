package top.ilovemyhome.peanotes.common.task.admin.dao.impl;

import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SqlGenerator;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.common.task.admin.application.AppContext;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobLogReportDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobLogReport;

import java.time.LocalDate;
import java.util.Map;

import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.toLocalDate;
import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.toLocalDateTime;

public class JobLogReportDaoImpl extends BaseDaoJdbiImpl<JobLogReport> implements JobLogReportDao {

    public JobLogReportDaoImpl(AppContext appContext) {
        super(TableDescription.builder()
            .withName("t_job_log_report")
            .withIdAutoGenerate(true)
            .withIdField("ID")
            .withFieldColumnMap(JobLogReport.FIELD_COLUMN_MAP)
            .build(), appContext.getDataSourceFactory().getJdbi());
    }

    @Override
    protected void registerRowMappers(Jdbi jdbi) {
        jdbi.registerRowMapper(JobLogReport.class, (rs, ctx) -> JobLogReport.builder()
            .withId(rs.getLong(JobLogReport.Field.id.getDbColumn()))
            .withTriggerDate(toLocalDate(rs.getDate(JobLogReport.Field.triggerDate.getDbColumn())))
            .withRunningCount(rs.getInt(JobLogReport.Field.runningCount.getDbColumn()))
            .withSucCount(rs.getInt(JobLogReport.Field.runningCount.getDbColumn()))
            .withFailCount(rs.getInt(JobLogReport.Field.failCount.getDbColumn()))
            .withLastUpdateDt(toLocalDateTime(rs.getTimestamp(JobLogReport.Field.lastUpdateDt.getDbColumn())))
            .build()
        );
    }

    @Override
    public int updateByTriggerDate(LocalDate triggerDate, JobLogReport jobLogReport) {
        String sql = getCachedSql(SqlGenerator.SQL_STATEMENT.updateAll)
            + " and trigger_date = :triggerDate";
        return update(sql, Map.of("triggerDate", triggerDate)
            , null
            , Map.of("t", jobLogReport));
    }
}
