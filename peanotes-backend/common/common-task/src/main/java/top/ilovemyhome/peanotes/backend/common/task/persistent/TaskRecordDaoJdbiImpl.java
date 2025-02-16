package top.ilovemyhome.peanotes.backend.common.task.persistent;

import com.fasterxml.jackson.core.type.TypeReference;
import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SqlGenerator;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.common.task.*;
import top.ilovemyhome.peanotes.backend.common.task.impl.AsyncTask;
import top.ilovemyhome.peanotes.backend.common.task.impl.SyncTask;
import top.ilovemyhome.peanotes.backend.common.task.impl.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.toLocalDateTime;
import static top.ilovemyhome.peanotes.backend.common.utils.StringConvertUtils.toEnum;

public class TaskRecordDaoJdbiImpl extends BaseDaoJdbiImpl<TaskRecord>
    implements TaskRecordDao {

    private static final String DEFAULT_TASK_TABLE_NAME = "t_task";
    private static final String DEFAULT_ID_SEQ_NAME = "seq_t_task_id";

    public TaskRecordDaoJdbiImpl(TaskContext taskContext) {
        super(TableDescription.builder()
            .withName(DEFAULT_TASK_TABLE_NAME)
            .withFieldColumnMap(TaskRecord.FIELD_COLUMN_MAP)
            .withIdField(TaskRecord.ID_FIELD)
            .withIdAutoGenerate(false)
            .build(), taskContext.getJdbi());
        this.taskContext = taskContext;
        this.taskContext.setTaskRecordDao(this);
    }

    @Override
    public void registerRowMappers(Jdbi jdbi) {
        jdbi.registerRowMapper(TaskRecord.class, (rs, ctx) -> {
            String successorIdStr = rs.getString(TaskRecord.Field.successorIds.getDbColumn());
            Set<Long> successorIds = JacksonUtil.fromJson(successorIdStr, new TypeReference<>() {});
            return TaskRecord.builder()
                .withId(rs.getLong(TaskRecord.Field.id.getDbColumn()))
                .withOrderKey(rs.getString(TaskRecord.Field.orderKey.getDbColumn()))
                .withName(rs.getString(TaskRecord.Field.name.getDbColumn()))
                .withDescription(rs.getString(TaskRecord.Field.description.getDbColumn()))
                .withExecutionKey(rs.getString(TaskRecord.Field.executionKey.getDbColumn()))
                .withSuccessorIds(successorIds)
                .withInput(rs.getString(TaskRecord.Field.input.getDbColumn()))
                .withOutput(rs.getString(TaskRecord.Field.output.getDbColumn()))
                .withAsync(rs.getBoolean(TaskRecord.Field.async.getDbColumn()))
                .withDummy(rs.getBoolean(TaskRecord.Field.dummy.getDbColumn()))
                .withCreateDt(toLocalDateTime(rs.getTimestamp(TaskRecord.Field.createDt.getDbColumn())))
                .withLastUpdateDt(toLocalDateTime(rs.getTimestamp(TaskRecord.Field.lastUpdateDt.getDbColumn())))
                .withStatus(toEnum(TaskStatus.class, rs.getString(TaskRecord.Field.status.getDbColumn())))
                .withStartDt(toLocalDateTime(rs.getTimestamp(TaskRecord.Field.startDt.getDbColumn())))
                .withEndDt(toLocalDateTime(rs.getTimestamp(TaskRecord.Field.endDt.getDbColumn())))
                .withSuccess(rs.getBoolean(TaskRecord.Field.success.getDbColumn()))
                .withFailReason(rs.getString(TaskRecord.Field.failReason.getDbColumn()))
                .withTimeout(rs.getLong(TaskRecord.Field.timeout.getDbColumn()))
                .withTimeoutUnit(toEnum(TimeUnit.class, rs.getString(TaskRecord.Field.timeoutUnit.getDbColumn())))
                .build();
        });
    }

    @Override
    public Long getNextId() {
        return jdbi.withHandle(h -> h.createQuery("select nextval('" + DEFAULT_ID_SEQ_NAME + "') ")
            .mapTo(Long.class)
            .one());
    }

    @Override
    public int deleteByOrderKey(String orderKey) {
        String sql = getCachedSql(SqlGenerator.SQL_STATEMENT.deleteAll)
            + " where ORDER_KEY = :orderKey ";
        return update(sql, Map.of("orderKey", orderKey), null);
    }

    @Override
    public <I, O> List<Task<I, O>> loadTaskForOrder(String orderKey) {
        String sql = getCachedSql(SqlGenerator.SQL_STATEMENT.selectAll)
            + " where ORDER_KEY = :orderKey ";
        List<TaskRecord> taskRecordList = find(sql, Map.of("orderKey", orderKey), null);
        return taskRecordList.stream().map(r -> {
            TaskInput<I> input = JacksonUtil.fromJson(r.getInput(), new TypeReference<>() {
            });
            TaskExecution<I, O> taskExecution = taskContext.getTaskFactory().createTaskForExecution(r.getExecutionKey());
            Task<I, O> task;
            if (r.isAsync()) {
                task = new AsyncTask<>(r.getId(), taskContext, r.getOrderKey(), r.getName()
                    , input, -1L, TimeUnit.MINUTES, taskExecution);
            } else {
                task = new SyncTask<>(r.getId(), taskContext, r.getOrderKey(), r.getName()
                    , input, r.getTimeout(), r.getTimeoutUnit(), taskExecution);
            }
            task.setSuccessorIds(r.getSuccessorIds());
            return task;
        }).collect(Collectors.toList());
    }

    @Override
    public <I, O> int createTasksForOrder(String orderKey, List<Task<I, O>> listOfTask) {
        AtomicInteger result = new AtomicInteger();
        jdbi.useTransaction(h -> {
            listOfTask.forEach(t -> {
                TaskRecord taskRecord = toTaskRecord(t);
                create(taskRecord);
                result.incrementAndGet();
            });
        });
        return result.get();
    }

    @Override
    public boolean isOrdered(String orderKey) {
        String sql = getCachedSql(SqlGenerator.SQL_STATEMENT.countAll)
            + " where ORDER_KEY = :orderKey";
        return count(sql, Map.of("orderKey", orderKey), null) > 0;
    }

    @Override
    public boolean isSuccess(String orderKey) {
        boolean ordered = isOrdered(orderKey);
        if (!ordered) {
            return false;
        }
        String sql = getCachedSql(SqlGenerator.SQL_STATEMENT.countAll)
            + " where ORDER_KEY = :orderKey  and STATUS != :status ";
        int nonSuccessCount = count(sql, Map.of("orderKey", orderKey, "status", TaskStatus.SUCCESS), null);
        return nonSuccessCount == 0;
    }

    @Override
    public <I> int start(Long id, TaskInput<I> input, LocalDateTime startDt) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(startDt);
        String sql = """
            update t_task set STATUS = :status , START_DT = :startDt , INPUT = :input
            where ID = :id
            """;
        return update(sql
            , Map.of("status", TaskStatus.RUNNING, "startDt", startDt, "id", id, "input", input)
            , null);
    }

    @Override
    public <O> int stop(Long id, TaskStatus newStatus, TaskOutput<O> output, LocalDateTime endDt) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(endDt);
        boolean success = newStatus == TaskStatus.SUCCESS;
        LocalDateTime now = LocalDateTime.now();
        String sql = """
            update t_task set STATUS = :status
            , LAST_UPDATE_DT = :lastUpdateDt
            , SUCCESS = :success
            , END_DT = :endDt
            """;

        Map<String, Object> normalParams = new HashMap<>();
        normalParams.put("id", id);
        normalParams.put("status", newStatus);
        normalParams.put("lastUpdateDt", now);
        normalParams.put("endDt", endDt);
        normalParams.put("success", success);

        if (Objects.nonNull(output.getOutput())) {
            sql = sql + " ,OUTPUT = :output ";
            normalParams.put("output", output.getOutput());
        }
        if (Objects.nonNull(output.getFailureReason())) {
            sql = sql + " ,FAIL_REASON = :failReason";
            normalParams.put("failReason", output.getFailureReason());
        }
        sql = sql + " where ID = :id ";
        return update(sql, normalParams, null);
    }

    @Override
    public String getTaskOrderByTaskId(Long taskId) {
        Objects.requireNonNull(taskId);
        return findOne(taskId).map(TaskRecord::getOrderKey).orElse(null);
    }


    private <I, O> TaskRecord toTaskRecord(Task<I, O> t) {
        Objects.requireNonNull(t);
        String input = JacksonUtil.toJson(t.getInput());
        TaskOutput<O> out = t.getOutput();
        String output = JacksonUtil.toJson(out.getOutput());
        return TaskRecord.builder()
            .withId(t.getId())
            .withOrderKey(t.getOrderKey())
            .withName(t.getName())
            .withDescription("")
            .withExecutionKey(t.getTaskExecution().getClass().getCanonicalName())
            .withSuccessorIds(t.getSuccessorIds())
            .withInput(input)
            .withOutput(output)
            .withAsync(t instanceof AsyncTask)
            .withDummy(false)
            .withCreateDt(t.getCreateDt())
            .withLastUpdateDt(t.getLastUpdateDt())
            .withStatus(t.getTaskStatus())
            .withStartDt(t.getStartDt())
            .withEndDt(t.getEndDt())
            .withSuccess(out.isSuccessful())
            .withFailReason(out.getFailureReason())
            .build();
    }

    protected final TaskContext taskContext;
}
