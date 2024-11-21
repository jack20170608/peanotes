package top.ilovemyhome.peanotes.common.task.admin.dao.impl;

import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.common.task.admin.application.AppContext;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobLogGlueDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobLogGlue;

import java.util.Map;

import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.toLocalDateTime;

public class JobLogGlueDaoImpl extends BaseDaoJdbiImpl<JobLogGlue> implements JobLogGlueDao {

    public JobLogGlueDaoImpl(AppContext appContext) {
        super(TableDescription.builder()
            .withName("t_job_logglue")
            .withIdField(JobLogGlue.Field.id.name())
            .withIdAutoGenerate(true)
            .withFieldColumnMap(JobLogGlue.FIELD_COLUMN_MAP)
            .build(), appContext.getDataSourceFactory().getJdbi());
    }

    @Override
    protected void registerRowMappers(Jdbi jdbi) {
        jdbi.registerRowMapper(JobLogGlue.class, (rs, ctx) -> JobLogGlue.builder()
            .withId(rs.getLong(JobLogGlue.Field.id.getDbColumn()))
            .withJobId(rs.getLong(JobLogGlue.Field.jobId.getDbColumn()))
            .withGlueType(rs.getString(JobLogGlue.Field.glueType.getDbColumn()))
            .withGlueSource(rs.getString(JobLogGlue.Field.glueSource.getDbColumn()))
            .withGlueRemark(rs.getString(JobLogGlue.Field.glueRemark.getDbColumn()))
            .withAddDt(toLocalDateTime(rs.getTimestamp(JobLogGlue.Field.addDt.getDbColumn())))
            .withLastUpdateDt(toLocalDateTime(rs.getTimestamp(JobLogGlue.Field.lastUpdateDt.getDbColumn())))
            .build()
        );
    }

    @Override
    public int deleteByJobId(Long jobId) {
        String sql = "delete from t_job_logglue where job_id = :jobId";
        return delete(sql, Map.of("jobId", jobId), null);
    }
}
