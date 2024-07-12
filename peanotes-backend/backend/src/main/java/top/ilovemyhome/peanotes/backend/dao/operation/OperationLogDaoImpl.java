package top.ilovemyhome.peanotes.backend.dao.operation;

import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.backend.domain.operation.OperationLogEntity;

import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.toLocalDateTime;
import static top.ilovemyhome.peanotes.backend.domain.operation.OperationLogEntity.Field;


public class OperationLogDaoImpl extends BaseDaoJdbiImpl<OperationLogEntity> implements OperationLogDao{

    public OperationLogDaoImpl(AppContext appContext) {
        super(TableDescription.builder()
            .withName("t_operation_log")
            .withIdField(OperationLogEntity.ID_FIELD)
            .withFieldColumnMap(OperationLogEntity.FIELD_COLUMN_MAP)
            .withIdAutoGenerate(true)
            .build()
            , appContext.getDataSourceFactory().getJdbi());

    }


    @Override
    public void registerRowMappers(Jdbi jdbi) {
        this.jdbi.registerRowMapper(OperationLogEntity.class, (rs, ctx) -> OperationLogEntity.builder()
            .withId(rs.getLong(Field.id.getDbColumn()))
            .withUserId(rs.getLong(Field.userId.getDbColumn()))
            .withCreateDt(toLocalDateTime(rs.getTimestamp(Field.createDt.getDbColumn())))
            .withUri(rs.getString(Field.uri.getDbColumn()))
            .withDetails(rs.getString(Field.details.getDbColumn()))
            .build());
    }
}
