package top.ilovemyhome.peanotes.common.task.admin.dao.impl;

import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils;
import top.ilovemyhome.peanotes.common.task.admin.application.AppContext;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobGroupDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobGroup;

import java.util.List;
import java.util.Map;

public class JobGroupDaoImpl extends BaseDaoJdbiImpl<JobGroup> implements JobGroupDao {

    public JobGroupDaoImpl(AppContext appContext) {
        super(TableDescription.builder()
            .withName("t_job_group")
            .withIdField(JobGroup.Field.id.name())
            .withFieldColumnMap(JobGroup.FIELD_COLUMN_MAP)
            .withIdAutoGenerate(true)
            .build(), appContext.getDataSourceFactory().getJdbi());
    }

    @Override
    protected void registerRowMappers(Jdbi jdbi) {
        this.jdbi.registerRowMapper(JobGroup.class, (rs, ctx) -> JobGroup.builder()
            .withId(rs.getLong(JobGroup.Field.id.getDbColumn()))
            .withAppName(rs.getString(JobGroup.Field.appName.getDbColumn()))
            .withTitle(rs.getString(JobGroup.Field.title.getDbColumn()))
            .withAddressType(rs.getInt(JobGroup.Field.addressType.getDbColumn()))
            .withAddressList(rs.getString(JobGroup.Field.addressList.getDbColumn()))
            .withUpdateTime(LocalDateUtils.toLocalDateTime(rs.getTimestamp(JobGroup.Field.updateTime.getDbColumn())))
            .build());
    }

    @Override
    public List<JobGroup> findByAddressType(int addressType) {
        String sql = """
            select * from t_job_group t where address_type= : address_type
            ORDER BY t.app_name, t.title, t.id ASC
            """;
        return find(sql, Map.of("address_type", addressType), null);
    }

}
