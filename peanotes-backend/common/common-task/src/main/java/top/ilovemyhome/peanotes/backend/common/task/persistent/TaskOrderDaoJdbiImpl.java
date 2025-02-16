package top.ilovemyhome.peanotes.backend.common.task.persistent;

import com.fasterxml.jackson.core.type.TypeReference;
import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SqlGenerator;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.common.task.impl.OrderType;
import top.ilovemyhome.peanotes.backend.common.task.TaskContext;
import top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils;


import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static top.ilovemyhome.peanotes.backend.common.utils.StringConvertUtils.toEnum;

public class TaskOrderDaoJdbiImpl extends BaseDaoJdbiImpl<TaskOrder> implements TaskOrderDao {

    public TaskOrderDaoJdbiImpl(TaskContext taskContext) {
        super(TableDescription.builder()
            .withName("t_task_order")
            .withIdAutoGenerate(true)
            .withFieldColumnMap(TaskOrder.FIELD_COLUMN_MAP)
            .withIdField(TaskOrder.ID_FIELD)
            .build(), taskContext.getJdbi());
        taskContext.setTaskOrderDao(this);
    }

    @Override
    public Optional<TaskOrder> findByKey(String key) {
        Objects.requireNonNull(key);
        String sql = getCachedSql(SqlGenerator.SQL_STATEMENT.selectAll)
            + " where key = :key ";
        return find(sql, Map.of("key", key), null).stream().findAny();
    }

    @Override
    public int updateByKey(String taskKey, TaskOrder task) {
        Objects.requireNonNull(task);
        Objects.requireNonNull(taskKey);
        String sql = """
            update t_task_order set
            name = :t.name
            , order_type = :t.order_type
            , attributes = :t.attributes
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
        jdbi.registerRowMapper(TaskOrder.class, (rs, ctx) -> TaskOrder.builder()
            .withId(rs.getLong(TaskOrder.Field.id.getDbColumn()))
            .withName(rs.getString(TaskRecord.Field.name.getDbColumn()))
            .withKey(rs.getString(TaskOrder.Field.key.getDbColumn()))
            .withOrderType(toEnum(OrderType.class, rs.getString(TaskOrder.Field.orderType.getDbColumn())))
            .withAttributes(JacksonUtil.fromJson(rs.getString(TaskOrder.Field.attributes.getDbColumn()), new TypeReference<>() {
            }))
            .withCreateDt(LocalDateUtils.toLocalDateTime(rs.getTimestamp(TaskRecord.Field.createDt.getDbColumn())))
            .withLastUpdateDt(LocalDateUtils.toLocalDateTime(rs.getTimestamp(TaskRecord.Field.lastUpdateDt.getDbColumn())))
            .build());
    }

}
