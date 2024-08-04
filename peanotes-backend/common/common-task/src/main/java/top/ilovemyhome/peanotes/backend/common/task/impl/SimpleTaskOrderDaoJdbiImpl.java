package top.ilovemyhome.peanotes.backend.common.task.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SqlGenerator;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.common.task.SimpleTaskOrder;
import top.ilovemyhome.peanotes.backend.common.task.SimpleTaskOrderDao;
import top.ilovemyhome.peanotes.backend.common.task.TaskContext;
import top.ilovemyhome.peanotes.backend.common.task.TaskRecord;
import top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils;


import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static top.ilovemyhome.peanotes.backend.common.utils.StringConvertUtils.toEnum;

public class SimpleTaskOrderDaoJdbiImpl extends BaseDaoJdbiImpl<SimpleTaskOrder> implements SimpleTaskOrderDao {

    public SimpleTaskOrderDaoJdbiImpl(TaskContext taskContext) {
        super(TableDescription.builder()
            .withName("t_task_order")
            .withIdAutoGenerate(true)
            .withFieldColumnMap(SimpleTaskOrder.FIELD_COLUMN_MAP)
            .withIdField(SimpleTaskOrder.ID_FIELD)
            .build(), taskContext.getJdbi());
        taskContext.setTaskOrderDao(this);
    }

    @Override
    public Optional<SimpleTaskOrder> findByKey(String key) {
        Objects.requireNonNull(key);
        String sql = getCachedSql(SqlGenerator.SQL_STATEMENT.selectAll)
            + " where key = :key ";
        return find(sql, Map.of("key", key), null).stream().findAny();
    }

    @Override
    public int updateByKey(String taskKey, SimpleTaskOrder task) {
        Objects.requireNonNull(task);
        Objects.requireNonNull(taskKey);
        String sql = """
            update t_task_order set
            name = :t.name
            , order_type = :t.order_type
            , other_keys = :t.otherKeysInJson
            , params = :t.paramsInJson
            , last_update_dt = :t.lastUpdateDt
            where key = :key
            """;
        return update(sql, Map.of("key", taskKey), null, Map.of("t", task));
    }

    @Override
    public int deleteByKey(String key) {
        Objects.requireNonNull(key);
        String sql = """
            delete from t_task_order where key = :key
            """;
        return delete(sql, Map.of("key", key) , null);
    }

    @Override
    public void registerRowMappers(Jdbi jdbi) {
        jdbi.registerRowMapper(SimpleTaskOrder.class, (rs, ctx) -> SimpleTaskOrder.builder()
            .withId(rs.getLong(SimpleTaskOrder.Field.id.getDbColumn()))
            .withName(rs.getString(TaskRecord.Field.name.getDbColumn()))
            .withKey(rs.getString(SimpleTaskOrder.Field.key.getDbColumn()))
            .withOrderType(toEnum(OrderType.class, rs.getString(SimpleTaskOrder.Field.orderType.getDbColumn())))
            .withOtherKeys(JacksonUtil.fromJson(rs.getString(SimpleTaskOrder.Field.otherKeysInJson.getDbColumn()), new TypeReference<>() {
            }))
            .withParams(JacksonUtil.fromJson(rs.getString(SimpleTaskOrder.Field.paramsInJson.getDbColumn()), new TypeReference<>() {
            }))
            .withCreateDt(LocalDateUtils.toLocalDateTime(rs.getTimestamp(TaskRecord.Field.createDt.getDbColumn())))
            .withLastUpdateDt(LocalDateUtils.toLocalDateTime(rs.getTimestamp(TaskRecord.Field.lastUpdateDt.getDbColumn())))
            .build());
    }

}
