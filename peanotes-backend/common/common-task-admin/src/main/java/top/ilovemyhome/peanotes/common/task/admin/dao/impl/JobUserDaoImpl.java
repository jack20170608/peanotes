package top.ilovemyhome.peanotes.common.task.admin.dao.impl;

import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.common.task.admin.application.AppContext;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobUserDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobRegistry;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobUser;


public class JobUserDaoImpl extends BaseDaoJdbiImpl<JobUser> implements JobUserDao {

    public JobUserDaoImpl(AppContext appContext) {
        super(TableDescription.builder()
            .withName("t_job_user")
            .withIdField("ID")
            .withIdAutoGenerate(true)
            .withFieldColumnMap(JobUser.FIELD_COLUMN_MAP)
            .build(), appContext.getDataSourceFactory().getJdbi());
    }

    @Override
    protected void registerRowMappers(Jdbi jdbi) {
        jdbi.registerRowMapper(JobRegistry.class, (rs, ctx) ->
            new JobUser(rs.getInt(JobUser.Field.id.getDbColumn())
                , rs.getString(JobUser.Field.username.getDbColumn())
                , rs.getString(JobUser.Field.password.getDbColumn())
                , rs.getInt(JobUser.Field.role.getDbColumn())
                , rs.getString(JobUser.Field.permission.getDbColumn())));
    }
}
