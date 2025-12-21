package top.ilovemyhome.benchmark.server.dao.impl;

import org.flywaydb.core.internal.util.JsonUtils;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;
import top.ilovemyhome.benchmark.server.application.AppContext;
import top.ilovemyhome.benchmark.server.dao.BenchmarkTestCaseDao;
import top.ilovemyhome.benchmark.si.BenchmarkTestCase;
import top.ilovemyhome.benchmark.si.DataSourceConfig;
import top.ilovemyhome.benchmark.si.enums.BenchmarkType;
import top.ilovemyhome.benchmark.si.enums.JdbcClientType;
import top.ilovemyhome.benchmark.si.enums.JdbcConnectionPoolType;
import top.ilovemyhome.commons.common.jackson.JacksonUtil;
import top.ilovemyhome.peanotes.commons.jdbi.TableDescription;
import top.ilovemyhome.peanotes.commons.jdbi.dao.BaseDaoJdbiImpl;

import java.sql.Types;
import java.util.Map;

import static top.ilovemyhome.commons.common.lang.LocalDateUtils.toLocalDateTime;
import static top.ilovemyhome.commons.common.lang.StringConvertUtils.toEnum;

public class BenchmarkTestCaseDaoJdbiImpl extends BaseDaoJdbiImpl<BenchmarkTestCase> implements BenchmarkTestCaseDao {

    public BenchmarkTestCaseDaoJdbiImpl(AppContext appContext) {
        super(TableDescription.builder()
                .withEntityClass(BenchmarkTestCase.class)
                .withName("benchmark_test_case")
                .withIdField("id")
                .withIdAutoGenerate(false)
                .withFieldColumnMap(Map.ofEntries(
                    Map.entry("id", "ID")
                    , Map.entry("name", "NAME")
                    , Map.entry("type", "TYPE")
                    , Map.entry("jdbcClientType", "JDBC_CLIENT_TYPE")
                    , Map.entry("connectionPoolType", "CONNECTION_POOL_TYPE")
                    , Map.entry("dataSourceConfig", "DATA_SOURCE_CONFIG")
                    , Map.entry("testRound", "TEST_ROUND")
                    , Map.entry("threadCount", "THREAD_COUNT")
                    , Map.entry("transactionPerThread", "TRANSACTION_PER_THREAD")
                    , Map.entry("createDt", "CREATE_DT")
                    , Map.entry("lastUpdateDt", "LAST_UPDATE_DT")
                ))
                .build()
            , appContext.getHikariDataSourceFactory().getJdbi());
    }

    @Override
    protected void registerRowMappers(Jdbi jdbi) {
        this.jdbi.registerRowMapper(BenchmarkTestCase.class, (rs, ctx) -> {
            return BenchmarkTestCase.builder()
                .withId(rs.getLong("ID"))
                .withName(rs.getString("NAME"))
                .withType(toEnum(BenchmarkType.class, rs.getString("TYPE")))
                .withJdbcClientType(toEnum(JdbcClientType.class, rs.getString("JDBC_CLIENT_TYPE")))
                .withConnectionPoolType(toEnum(JdbcConnectionPoolType.class, rs.getString("CONNECTION_POOL_TYPE")))
                .withDataSourceConfig(JsonUtils.parseJson(rs.getString("DATA_SOURCE_CONFIG"), DataSourceConfig.class))
                .withTestRound(rs.getInt("TEST_ROUND"))
                .withThreadCount(rs.getInt("THREAD_COUNT"))
                .withTransactionPerThread(rs.getInt("TRANSACTION_PER_THREAD"))
                .withCreateDt(toLocalDateTime(rs.getTimestamp("CREATE_DT")))
                .withLastUpdateDt(toLocalDateTime(rs.getTimestamp("LAST_UPDATE_DT")))
                .build();
        });
        //Register DataSourceConfig argument factory
        jdbi.registerArgument(new AbstractArgumentFactory<DataSourceConfig>(Types.VARCHAR) {
            @Override
            protected Argument build(DataSourceConfig value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, JacksonUtil.toJson(value));
            }
        });
    }
}
