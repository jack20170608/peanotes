package top.ilovemyhome.peanotes.backend.web.handlers;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.muserver.rest.Required;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SearchCriteria;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Direction;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Page;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.impl.PageRequest;
import top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils;
import top.ilovemyhome.peanotes.backend.dao.order.OrderDao;
import top.ilovemyhome.peanotes.backend.dao.order.OrderDuckdbDaoImpl;
import top.ilovemyhome.peanotes.backend.dao.order.OrderPostgresDaoImpl;
import top.ilovemyhome.peanotes.backend.web.dto.common.Order;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Function;

@Path("duckdb")
public class DuckdbPocHandler {

    private final Connection duckDbInMemConn;
    private final Connection pgConn;
    private final ExecutorService executorService;
    private final Connection duckDbFileConn;

    public enum DbType {
        duck_db_in_memory, duck_db_file, pg_db
    }

    public DuckdbPocHandler(AppContext appContext) {
        try {
            this.orderDuckdbDao = appContext.getBean("orderDuckdbDao", OrderDuckdbDaoImpl.class);
            this.orderPostgresDao = appContext.getBean("orderPostgresDao", OrderPostgresDaoImpl.class);

            Class.forName("org.duckdb.DuckDBDriver");
            //In memory database
            this.duckDbInMemConn = DriverManager.getConnection("jdbc:duckdb:");
            this.pgConn = DriverManager.getConnection("jdbc:postgresql://10.10.10.5:5432/peanotes", "app_user", "1");
            this.duckDbFileConn = DriverManager.getConnection("jdbc:duckdb:D:\\data\\duckdb\\peanotes");
            this.executorService = Executors.newFixedThreadPool(8, new ThreadFactoryBuilder()
                .setNameFormat("duckdb-pool-%d")
                .setDaemon(true)
                .build());
            initSchema();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }


    @PUT
    @Path("/createData")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.WILDCARD)
    public Response createData(@QueryParam("dbType") @Required @DefaultValue("pg_db") DbType dbType
        , @QueryParam("dataSize") @DefaultValue("10000") int dataSize) {

        int batchSize = 10000;
        int batchCount = dataSize / batchSize + (dataSize % batchSize > 0 ? 1 : 0);
        List<Future<Boolean>> futures = Lists.newArrayList();

        //don't support multiple insertion
        switch (dbType) {
            case pg_db -> {
                for (int i = 1; i <= batchCount; i++) {
                    futures.add(executorService.submit(() -> insertDataTask.apply(getConn(DbType.pg_db))));
                }
                futures.forEach(f -> {
                    try {
                        Boolean success = f.get();
                        System.out.println(success);
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            case duck_db_in_memory -> {
                for (int i = 1; i <= batchCount; i++) {
                    boolean success = insertDataTask.apply(duckDbInMemConn);
                    logger.info("Batch=[{}/{}], success=[{}].", i, batchCount, success);
                }
            }
            case duck_db_file -> {
                for (int i = 1; i <= batchCount; i++) {
                    List<Long> ids = orderDuckdbDao.getNextIds(batchSize);
                    List<Order> orders = Objects.requireNonNull(Order.randomObj(ids, 10000)).toList();
                    orders.forEach(o -> {
                        orderDuckdbDao.create(o);
                    });
                }
            }
        }

        logger.info("Success");
        return Response.ok("success").build();
    }


    @PUT
    @Path("/loadDataFromParquetFile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.WILDCARD)
    public Response loadData(@QueryParam("dbType") DbType dbType
        , @QueryParam("filePath") @DefaultValue("D:\\data\\duckdb\\order.parquet") String filePath) {
        String sql = """
            INSERT INTO t_order
                SELECT * FROM read_parquet(?);
            """;
        if (dbType.equals(DbType.pg_db)) {
            throw new IllegalStateException("Not supported operation!");
        }
        StopWatch sw = new StopWatch();
        sw.start();
        try (PreparedStatement prpt = getConn(dbType).prepareStatement(sql)) {
            prpt.setString(1, filePath);
            prpt.execute();
        } catch (SQLException e) {
            logger.error("Error", e);
        }
        sw.stop();
        logger.info("Success with timecost={}.", sw.getTime(TimeUnit.SECONDS));
        return Response.ok("success").build();
    }

    @GET
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    public Response query(@QueryParam("dbType") @DefaultValue("duck_db_file") DbType dbType
        , @QueryParam("page") @DefaultValue("0") int page
        , @QueryParam("pageSize") @DefaultValue("1000") int pageSize
        , @QueryParam("sortBy") @DefaultValue("ID") String sortBy
        , @QueryParam("direction") @DefaultValue("ASC") Direction direction
        , @QueryParam("minPrice") @DefaultValue("1.00")BigDecimal minPrice
        , @QueryParam("maxPrice") @DefaultValue("1000.00") BigDecimal maxPrice
        , @QueryParam("minValueDate") @DefaultValue("2022-02-02") LocalDate minValueDate
        , @QueryParam("maxValueDate") @DefaultValue("2023-03-03") LocalDate maxValueDate) {
        logger.info("query price [{},{}], value date [{},{}].", minPrice, maxPrice, minValueDate, maxValueDate);
        StopWatch sw = new StopWatch();
        sw.start();
        PageRequest pageRequest = new PageRequest(page, pageSize, direction, sortBy);
        SearchCriteria searchCriteria = new SearchCriteria() {
            @Override
            public Map<String, Object> normalParams() {
                return Map.of("minPrice", minPrice, "maxPrice", maxPrice, "minValueDate", minValueDate, "maxValueDate", maxValueDate);
            }

            @Override
            public String whereClause() {
                return " where price between :minPrice and :maxPrice and value_date between :minValueDate and :maxValueDate";
            }
        };
        Page<Order> result = null;
        switch (dbType){
            case duck_db_file ->
                result = orderDuckdbDao.find(searchCriteria, pageRequest);
            case pg_db ->
                result = orderPostgresDao.find(searchCriteria, pageRequest);
            default ->
                throw new IllegalStateException("Not supported operation!");
        }
        sw.stop();
        logger.info("@@@@@ Time cost=[{}ms].", sw.getTime(TimeUnit.MILLISECONDS));
        return Response.ok(result).build();
    }

    @GET
    @Path("/exportToFile")
    public Response exportToFile() {
        StopWatch sw = new StopWatch();
        sw.start();
        try (Statement stmt = duckDbFileConn.createStatement()) {
            stmt.execute("COPY t_order TO 'output2.parquet' (FORMAT PARQUET);");
        } catch (SQLException e) {
            logger.error("Error in exportToFile", e);
        }
        sw.stop();
        logger.info("export timeCost=[{}]", sw.getTime(TimeUnit.MILLISECONDS));

        return Response.ok("success").build();
    }

    private Connection getConn(DbType dbType) {
        Connection connection = null;
        switch (dbType) {
            case duck_db_in_memory -> connection = duckDbInMemConn;
            case duck_db_file -> connection = duckDbFileConn;
            case pg_db -> connection = pgConn;
        }
        return connection;
    }


    private final Function<Connection, Boolean> insertDataTask = new Function<>() {
        @Override
        public Boolean apply(Connection conn) {
            boolean success = false;
            try (PreparedStatement prpt = conn.prepareStatement(insertDataSql)) {
                Objects.requireNonNull(Order.randomObj(null, 10000)).toList().forEach(o -> {
                    try {
                        prpt.setString(1, o.sequenceNo());
                        prpt.setInt(2, o.customerId());
                        prpt.setInt(3, o.productId());
                        prpt.setDate(4, LocalDateUtils.toSqlDate(o.valueDate()));
                        prpt.setBigDecimal(5, o.price());
                        prpt.setInt(6, o.quantity());
                        prpt.setBigDecimal(7, o.value());
                        prpt.setTimestamp(8, LocalDateUtils.toSqlTimestamp(o.createDt()));
                        prpt.setTimestamp(9, LocalDateUtils.toSqlTimestamp(o.lastUpdateDt()));
                        prpt.addBatch();
                    } catch (SQLException e) {
                        logger.error("Error in insert data", e);
                    }
                });
                prpt.executeBatch();
                success = true;
            } catch (SQLException e) {
                throw new IllegalStateException("Error in insert data", e);
            }
            return success;
        }
    };

    private final List<String> createTables = List.of(
        "DROP TABLE IF EXISTS t_order;"
        , "DROP SEQUENCE IF EXISTS seq_t_order_id;"
        , "CREATE SEQUENCE IF NOT EXISTS seq_t_order_id START 1 ;"
        , """
                    CREATE TABLE t_order(
                        id    bigint PRIMARY KEY DEFAULT nextval('seq_t_order_id'),
                        sequence_no VARCHAR(64) NOT NULL,
                        customer_id INTEGER NOT NULL,
                        product_id INTEGER NOT NULL ,
                        value_date DATE,
                        price    DECIMAL ,
                        quality INTEGER NOT NULL DEFAULT 0,
                        value    DECIMAL,
                        create_dt  TIMESTAMP,
                        last_update_dt TIMESTAMP
                    );
            """
    );
    private final String insertDataSql = """
        insert into t_order (
            sequence_no, customer_id, product_id, value_date, price, quality, value, create_dt, last_update_dt
        )values(?,?,?,?,?,?,?,?,?);
        """;

    private void initSchema() {
        //The in memory duckdb
        try (Statement stmt = duckDbInMemConn.createStatement()) {
            createTables.forEach(sql -> {
                try {
                    boolean success = stmt.execute(sql);
                    logger.info("sql= {}, Success = {}", sql, success);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Throwable t) {
            throw new IllegalStateException("Error in initSchema in duckdb.", t);
        }

        //The file based duckdb
//        try (Statement stmt = duckDbFileConn.createStatement()) {
//            createTables.forEach(sql -> {
//                try {
//                    boolean success = stmt.execute(sql);
//                    logger.info("sql= {}, Success = {}", sql, success);
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        } catch (Throwable t) {
//            throw new IllegalStateException("Error in initSchema in duckdb.", t);
//        }

        logger.info("--===================================================");
        try (Statement stmt = pgConn.createStatement()) {
            createTables.forEach(sql -> {
                try {
                    stmt.execute(sql);
                    logger.info("sql= {}, Success = {}", sql, true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Throwable t) {
            throw new IllegalStateException("Error in initSchema in pgdb", t);
        }

    }

    private final OrderDao orderDuckdbDao;
    private final OrderDao orderPostgresDao;

    private static final Logger logger = LoggerFactory.getLogger(DuckdbPocHandler.class);
}
