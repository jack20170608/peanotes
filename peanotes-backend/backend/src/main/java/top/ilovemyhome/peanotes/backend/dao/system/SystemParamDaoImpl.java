package top.ilovemyhome.peanotes.backend.dao.system;

import top.ilovemyhome.peanotes.backend.application.AppContext;
import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.backend.domain.system.SystemParamEntity;

import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.toLocalDateTime;


public class SystemParamDaoImpl extends BaseDaoJdbiImpl<SystemParamEntity> implements SystemParamDao {

    public SystemParamDaoImpl(AppContext appContext) {
        super(TableDescription.builder()
            .withName("t_sys_param")
            .withIdField(SystemParamEntity.ID_FIELD)
            .withFieldColumnMap(SystemParamEntity.FIELD_COLUMN_MAP)
            .withIdAutoGenerate(true)
            .build(), appContext.getDataSourceFactory().getJdbi());
    }

    @Override
    public void registerRowMappers(Jdbi jdbi) {
        this.jdbi.registerRowMapper(SystemParamEntity.class, (rs, ctx) -> SystemParamEntity.builder()
            .withId(rs.getLong(SystemParamEntity.Field.id.getDbColumn()))
            .withParamName(rs.getString(SystemParamEntity.Field.paramName.getDbColumn()))
            .withParamValue(rs.getString(SystemParamEntity.Field.paramValue.getDbColumn()))
            .withParamDesc(rs.getString(SystemParamEntity.Field.paramDesc.getDbColumn()))
            .withCreateDt(toLocalDateTime(rs.getTimestamp(SystemParamEntity.Field.createDt.getDbColumn())))
            .withUpdateDt(toLocalDateTime(rs.getTimestamp(SystemParamEntity.Field.updateDt.getDbColumn())))
            .build());
    }
}
