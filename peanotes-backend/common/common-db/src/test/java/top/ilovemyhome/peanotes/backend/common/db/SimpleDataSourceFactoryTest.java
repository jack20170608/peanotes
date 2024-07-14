package top.ilovemyhome.peanotes.backend.common.db;

import com.typesafe.config.Config;
import org.jdbi.v3.core.*;
import org.jdbi.v3.core.statement.Update;
import org.jdbi.v3.core.transaction.LocalTransactionHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.config.ConfigLoader;
import top.ilovemyhome.peanotes.backend.common.db.utils.TestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.toLocalDateTime;

@Disabled
public class SimpleDataSourceFactoryTest {

    @BeforeAll
    public static void setup() {
        dataSourceFactory = TestUtils.getDataSourceFactory();
    }

    @Test
    public void test() {
        Jdbi jdbi = dataSourceFactory.getJdbi();
        String version = jdbi.withHandle(handle -> handle.select("select version() as version ;")
            .mapTo(String.class)
            .one());
        LOGGER.info("Database version: {}", version);
    }

    @Test
    public void testLike() {
        Jdbi jdbi = dataSourceFactory.getJdbi();
        List<String> paraNames = jdbi.withHandle(handle -> handle.select("select param_name from t_sys_param where param_name like :keyword" +
                " or param_value like :keyword or param_desc like :keyword ")
            .bind("keyword", "%peanote%")
            .mapTo(String.class).list());
        System.out.println(paraNames);
    }

    @Test
    public void testJdbiTransactionSample() {
        Jdbi jdbi = dataSourceFactory.getJdbi();
        Handle handle = jdbi.open();
        try {
            handle.begin();
            int r1 = handle.execute("""
                    insert into t_sys_param (param_name, param_value, param_desc, create_dt, update_dt) values (?, ? ,? ,? ,?)
                    """
                , "pay_no", "1212312", "a dummy pay number", LocalDateTime.now(), LocalDateTime.now());
            int r2 = handle.execute("delete from t_sys_param where 1 = 0; ");
            LOGGER.info("r1 is {}, r2 is {}", r1, r2);
            handle.commit();
        } catch (Throwable t) {
            LOGGER.error("Error, Rollback!", t);
            handle.rollback();
        }finally {
            handle.close();
        }
    }


    @Test
    public void testAopTransactionSample() {
        Jdbi jdbi = dataSourceFactory.getJdbi();
        HandleCallback<Integer, RuntimeException> handleCallback = h -> {
            int result = h.execute("insert into t_sys_param(param_name, param_value, param_desc)" +
                " values(?,?,?)", "jack", "jack888", "testString");
            LOGGER.info("Result is {}", result);

            Update update = h.createUpdate("update t_sys_param set param_value = :v where param_name = :n");
            update.bind("v", "jack666");
            update.bind("n", "jack");
            result = update.execute();

            String version = h.createQuery("select version1() as version").mapTo(String.class).one();
            LOGGER.info("version is {}", version);
            return result;
        };
        int updateRows = jdbi.inTransaction(handleCallback);
        LOGGER.info("updateRows is {}.", updateRows);
    }

    private static SimpleDataSourceFactory dataSourceFactory;

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDataSourceFactoryTest.class);
}
