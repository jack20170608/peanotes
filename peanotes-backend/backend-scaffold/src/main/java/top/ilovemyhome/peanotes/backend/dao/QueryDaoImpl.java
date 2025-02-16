package top.ilovemyhome.peanotes.backend.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import org.apache.commons.lang3.time.DurationUtils;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils;
import top.ilovemyhome.peanotes.backend.domain.QueryResultV1;
import top.ilovemyhome.peanotes.backend.domain.QueryResultV2;
import top.ilovemyhome.peanotes.backend.domain.ResultFormat;

import java.sql.*;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QueryDaoImpl extends BaseDaoJdbiImpl<QueryResultV1> implements QueryDao {


    public QueryDaoImpl(AppContext appContext) {
        super(null, appContext.getDataSourceFactory().getJdbi());
        Config config = appContext.getConfig();
        if (config.hasPath("query.maxQueryResultSize")) {
            this.maxQueryResultSize = config.getInt("query.maxQueryResultSize");
        } else {
            this.maxQueryResultSize = DEFAULT_MAX_QUERY_RESULT_SIZE;
        }
        if (config.hasPath("query.maxQueryDuration")) {
            this.resultFormat = ResultFormat.valueOf(config.getString("query.resultFormat"));
        } else {
            this.resultFormat = ResultFormat.JSON;
        }
    }

    @Override
    protected void registerRowMappers(Jdbi jdbi) {
    }

    @Override
    public QueryResultV1 queryV1(String sql) {
        return (QueryResultV1) doQuery(sql, ResultFormat.JSON);
    }

    @Override
    public QueryResultV2 queryV2(String sql) {
        return (QueryResultV2) doQuery(sql, ResultFormat.TEXT);
    }


    private Object doQuery(String sql, ResultFormat resultFormat) {
        log.info("Query sql={}, resultFormat={}", sql, resultFormat);
        final ResultFormat rsFormat = Objects.isNull(resultFormat) ? this.resultFormat : resultFormat;
        Object queryResult = null;
        int fetchSize = Math.min(maxQueryResultSize, DEFAULT_MAX_QUERY_RESULT_SIZE);
        try (Handle handle = jdbi.open()) {
            queryResult = handle.createQuery(sql)
                .setFetchSize(fetchSize)
                .setMaxRows(maxQueryResultSize)
                .execute(((statementSupplier, ctx) -> {
                    try (ctx) {
                        long startTs = System.currentTimeMillis();
                        PreparedStatement ps = statementSupplier.get();
                        ResultSet rs = ps.executeQuery();
                        ResultSetMetaData rsMetaData = rs.getMetaData();
                        Duration timeCost = Duration.ofMillis(System.currentTimeMillis() - startTs);
                        switch (rsFormat) {
                            case JSON -> {
                                return QueryResultV1.builder()
                                    .data(prepareJsonData(rsMetaData, rs))
                                    .build();
                            }
                            case TEXT -> {
                                List<Object[]> data = prepareTextData(rsMetaData, rs);
                                List<String> columns = Lists.newArrayList();
                                for (int i = rsMetaData.getColumnCount(); i >= 1; i--) {
                                    columns.add(rsMetaData.getColumnName(i));
                                }
                                return QueryResultV2.builder()
                                    .data(data)
                                    .columns(columns)
                                    .rowCount(data.size())
                                    .timeCost(timeCost.toString())
                                    .build();
                            }
                            case null, default -> {
                                throw new SQLException("Not supported result format: " + this.resultFormat);
                            }
                        }
                    }
                }));
        }
        return queryResult;
    }

    private List<Map<String, Object>> prepareJsonData(ResultSetMetaData rsMetaData, ResultSet rs) throws SQLException {
        List<Map<String, Object>> data = Lists.newArrayList();
        int columnSize = rsMetaData.getColumnCount();
        int rowCounter = 0;
        while (rs.next() && rowCounter < maxQueryResultSize) {
            Map<String, Object> row = Maps.newTreeMap();
            for (int i = 1; i <= columnSize; i++) {
                //for some special type
                switch (rsMetaData.getColumnType(i)) {
                    case Types.TIMESTAMP ->
                        row.put(rsMetaData.getColumnName(i), LocalDateUtils.toLocalDateTime(rs.getTimestamp(i)));
                    case Types.DATE ->
                        row.put(rsMetaData.getColumnName(i), LocalDateUtils.toLocalDate(rs.getDate(i)));
                    case Types.CLOB ->
                        row.put(rsMetaData.getColumnName(i), "CLOB");
                    case Types.BLOB ->
                        row.put(rsMetaData.getCatalogName(i), "BLOB");
                    default -> row.put(rsMetaData.getColumnName(i), rs.getObject(i));
                }
            }
            data.add(row);
            rowCounter++;
        }
        return data;
    }

    private List<Object[]> prepareTextData(ResultSetMetaData rsMetaData, ResultSet rs) throws SQLException {
        List<Object[]> data = Lists.newArrayList();
        int columnSize = rsMetaData.getColumnCount();
        int rowCounter = 0;
        while (rs.next() && rowCounter < maxQueryResultSize) {
            Object[] row = new Object[columnSize];
            int j = 0;
            for (int i = columnSize; i >= 1; i--) {
                row[j++] = rs.getObject(i);
                //for some special type

            }
            data.add(row);
            rowCounter++;
        }
        return data;
    }

    private static final Logger log = LoggerFactory.getLogger(QueryDaoImpl.class);

    private final int maxQueryResultSize;
    private final ResultFormat resultFormat;

    public static final int DEFAULT_MAX_QUERY_RESULT_SIZE = 2000;
    public static final ResultFormat DEFAULT_RESULT_FORMAT = ResultFormat.JSON;
}
