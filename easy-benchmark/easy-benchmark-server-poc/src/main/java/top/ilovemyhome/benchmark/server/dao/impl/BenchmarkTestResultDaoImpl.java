package top.ilovemyhome.benchmark.server.dao.impl;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import top.ilovemyhome.benchmark.server.application.AppContext;
import top.ilovemyhome.benchmark.server.dao.BenchmarkTestResultDao;
import top.ilovemyhome.benchmark.si.BenchmarkTestResult;
import top.ilovemyhome.benchmark.si.enums.State;
import top.ilovemyhome.peanotes.commons.jdbi.TableDescription;
import top.ilovemyhome.peanotes.commons.jdbi.dao.BaseDaoJdbiImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static top.ilovemyhome.commons.common.lang.LocalDateUtils.toLocalDateTime;
import static top.ilovemyhome.commons.common.lang.StringConvertUtils.toEnum;

public class BenchmarkTestResultDaoImpl extends BaseDaoJdbiImpl<BenchmarkTestResult>
    implements BenchmarkTestResultDao {


    public BenchmarkTestResultDaoImpl(AppContext appContext) {
        super(TableDescription.builder()
            .withEntityClass(BenchmarkTestResult.class)
            .withName("benchmark_test_result")
            .withIdField("id")
            .withIdAutoGenerate(false)
            .withFieldColumnMap(Map.ofEntries(
                Map.entry("id", "ID"),
                Map.entry("testCaseId", "TEST_CASE_ID"),
                Map.entry("startDt", "START_DT"),
                Map.entry("endDt", "END_DT"),
                Map.entry("totalTimeMilliseconds", "TOTAL_TIME_MS"),
                Map.entry("state", "STATE"),
                Map.entry("success", "SUCCESS"),
                Map.entry("errorMessage", "ERROR_MSG"),
                Map.entry("errorStackTrace", "ERROR_STACK_TRACE"),
                Map.entry("testRound", "TEST_ROUND"),
                Map.entry("totalThreadCount", "TOTAL_THREAD_COUNT"),
                Map.entry("failedThreadCount", "FAILED_THREAD_COUNT"),
                Map.entry("successThreadCount", "SUCCESS_THREAD_COUNT"),
                Map.entry("totalTransactionCount", "TOTAL_TRANSACTION_COUNT"),
                Map.entry("failedTransactionCount", "FAILED_TRANSACTION_COUNT"),
                Map.entry("rollbackTransactionCount", "ROLLBACK_TRANSACTION_COUNT"),
                Map.entry("avgCommitTimeMilliseconds", "AVG_COMMIT_TIME_MS"),
                Map.entry("avgRollbackTimeMilliseconds", "AVG_ROLLBACK_TIME_MS"),
                Map.entry("tps", "TPS"),
                Map.entry("successRate", "SUCCESS_RATE"),
                Map.entry("createDt", "CREATE_DT")
            ))
            .build()
        , appContext.getHikariDataSourceFactory().getJdbi());
    }

    @Override
    protected void registerRowMappers(Jdbi jdbi) {
        this.jdbi.registerRowMapper(BenchmarkTestResult.class, new RowMapper<BenchmarkTestResult>() {
            @Override
            public BenchmarkTestResult map(ResultSet rs, StatementContext ctx) throws SQLException {
                return BenchmarkTestResult.builder()
                    .withId(rs.getLong("ID"))
                    .withTestCaseId(rs.getLong("TEST_CASE_ID"))
                    .withStartDt(toLocalDateTime(rs.getTimestamp("START_DT")))
                    .withEndDt(toLocalDateTime(rs.getTimestamp("END_DT")))
                    .withTotalTimeMilliseconds(rs.getLong("TOTAL_TIME_MS"))
                    .withState(toEnum(State.class, rs.getString("STATE")))
                    .withSuccess(rs.getBoolean("SUCCESS"))
                    .withErrorMessage(rs.getString("ERROR_MSG"))
                    .withErrorStackTrace(rs.getString("ERROR_STACK_TRACE"))
                    .withTestRound(rs.getInt("TEST_ROUND"))
                    .withTotalThreadCount(rs.getInt("TOTAL_THREAD_COUNT"))
                    .withFailedThreadCount(rs.getInt("FAILED_THREAD_COUNT"))
                    .withSuccessThreadCount(rs.getInt("SUCCESS_THREAD_COUNT"))
                    .withTotalTransactionCount(rs.getInt("TOTAL_TRANSACTION_COUNT"))
                    .withCommitTransactionCount(rs.getInt("COMMIT_TRANSACTION_COUNT"))
                    .withRollbackTransactionCount(rs.getInt("ROLLBACK_TRANSACTION_COUNT"))
                    .withAvgCommitTimeMilliseconds(rs.getBigDecimal("AVG_COMMIT_TIME_MS"))
                    .withAvgRollbackTimeMilliseconds(rs.getBigDecimal("AVG_ROLLBACK_TIME_MS"))
                    .withTps(rs.getBigDecimal("TPS"))
                    .withSuccessRate(rs.getBigDecimal("SUCCESS_RATE"))
                    .withCreateDt(toLocalDateTime(rs.getTimestamp("CREATE_DT")))
                    .withLastUpdateDt(toLocalDateTime(rs.getTimestamp("LAST_UPDATE_DT")))
                    .build();
            }
        });
    }
}
