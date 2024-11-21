package top.ilovemyhome.peanotes.common.task.admin.dao.impl;

import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SearchCriteria;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.common.task.admin.application.AppContext;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobRegistryDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobRegistry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.toLocalDateTime;

public class JobRegistryDaoImpl extends BaseDaoJdbiImpl<JobRegistry> implements JobRegistryDao {

    public JobRegistryDaoImpl(AppContext appContext) {
        super(TableDescription.builder()
            .withName("t_job_registry")
            .withIdAutoGenerate(true)
            .withFieldColumnMap(JobRegistry.FIELD_COLUMN_MAP)
            .withIdField(JobRegistry.Field.id.name())
            .build(), appContext.getDataSourceFactory().getJdbi());
    }

    @Override
    protected void registerRowMappers(Jdbi jdbi) {
        jdbi.registerRowMapper(JobRegistry.class, (rs, ctx) -> JobRegistry.builder()
            .withId(rs.getLong(JobRegistry.Field.id.getDbColumn()))
            .withRegistryGroup(rs.getString(JobRegistry.Field.registryGroup.getDbColumn()))
            .withRegistryKey(rs.getString(JobRegistry.Field.registryKey.getDbColumn()))
            .withRegistryValue(rs.getString(JobRegistry.Field.registryValue.getDbColumn()))
            .withLastUpdateDt(toLocalDateTime(rs.getTimestamp(JobRegistry.Field.lastUpdateDt.getDbColumn())))
            .build());
    }

    @Override
    public List<Long> findDead(LocalDateTime minLastUpdateDt) {
        return findIds(new SearchCriteria() {
            @Override
            public String whereClause() {
                return " where last_update_dt < :minLastUpdateDt ";
            }

            @Override
            public Map<String, Object> normalParams() {
                return Map.of("minLastUpdateDt", minLastUpdateDt);
            }
        });
    }

    @Override
    public List<JobRegistry> findAll(LocalDateTime minLastUpdateDt) {
        return find("select * from t_job_registry where last_update_dt >= :minLastUpdateDt "
            , Map.of("minLastUpdateDt", minLastUpdateDt), null);
    }

    @Override
    public int registryUpdate(LocalDateTime lastUpdateDt, String registryGroup, String registryKey, String registryValue) {
        return update("""
                update t_job_registry set
                last_update_dt = :lastUpdateDt
                where 1=1
                and registry_group = :registryGroup
                and registry_key = :registryKey
                and registry_value = :registryValue
            """, Map.of("lastUpdateDt", lastUpdateDt, "registryGroup", registryGroup, "registryKey", registryKey, "registryValue", registryValue)
        );
    }

    @Override
    public int registryDelete(String registryGroup, String registryKey, String registryValue) {
        return delete("""
            delete from t_job_registry
                where 1=1
                and registry_group = :registryGroup
                and registry_key = :registryKey
                and registry_value = :registryValue
            """, Map.of("registryGroup", registryGroup, "registryKey", registryKey, "registryValue", registryValue), null);
    }

    @Override
    public Long registrySave(LocalDateTime lastUpdateDt, String registryGroup, String registryKey, String registryValue) {
        JobRegistry jobRegistry = JobRegistry.builder()
            .withLastUpdateDt(lastUpdateDt)
            .withRegistryGroup(registryGroup)
            .withRegistryKey(registryKey)
            .withRegistryValue(registryValue)
            .build();
        return create(jobRegistry);
    }


}
