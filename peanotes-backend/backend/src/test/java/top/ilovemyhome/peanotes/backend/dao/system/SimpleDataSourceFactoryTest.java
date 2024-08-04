package top.ilovemyhome.peanotes.backend.dao.system;

import com.typesafe.config.Config;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.config.ConfigLoader;
import top.ilovemyhome.peanotes.backend.common.db.SimpleDataSourceFactory;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Direction;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Page;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.impl.PageImpl;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.impl.PageRequest;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.domain.system.SystemParamEntity;
import top.ilovemyhome.peanotes.backend.application.AppContext;

import java.util.List;

import static org.mockito.Mockito.when;
import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.toLocalDateTime;

@Disabled
public class SimpleDataSourceFactoryTest {

    @BeforeAll
    public static void initConfig(){
        config = ConfigLoader.loadConfig("config/application.conf"
            , "config/application-test.conf");
        dataSourceFactory = SimpleDataSourceFactory.getInstance(config);
        jdbi = dataSourceFactory.getJdbi();
        jdbi.registerRowMapper(SystemParamEntity.class, (rs, ctx) -> SystemParamEntity.builder()
            .withId(rs.getLong(SystemParamEntity.Field.id.getDbColumn()))
            .withParamName(rs.getString(SystemParamEntity.Field.paramName.getDbColumn()))
            .withParamValue(rs.getString(SystemParamEntity.Field.paramValue.getDbColumn()))
            .withParamDesc(rs.getString(SystemParamEntity.Field.paramDesc.getDbColumn()))
            .withCreateDt(toLocalDateTime(rs.getTimestamp(SystemParamEntity.Field.createDt.getDbColumn())))
            .withUpdateDt(toLocalDateTime(rs.getTimestamp(SystemParamEntity.Field.updateDt.getDbColumn())))
            .build());

        appContext = Mockito.mock(AppContext.class);
        when(appContext.getDataSourceFactory()).thenReturn(dataSourceFactory);
    }


    @Test
    public void testMapper(){
        String whereCondition = " where param_name like :keyword or param_value like :keyword or param_desc like '%'||:keyword||'%'";
        String pageSql = "SELECT * FROM t_sys_param" + whereCondition + " ORDER BY ID ASC LIMIT 100 OFFSET 0";
        String countSql = "SELECT COUNT(*) FROM t_sys_param" + whereCondition;
        String keyword = "peanote";
        Long count = jdbi.withHandle(handle -> handle.select(countSql)
            .bind("keyword",keyword)
            .mapTo(Long.class).one());

        List<SystemParamEntity> paramEntities = jdbi.withHandle(handle -> handle.select(pageSql)
            .bind("keyword", keyword)
            .mapTo(SystemParamEntity.class).list());
        System.out.println(paramEntities.size());
        System.out.println(JacksonUtil.toJson(paramEntities));

        PageRequest pageRequest = new PageRequest(0,100, Direction.ASC, "ID");
        Page<SystemParamEntity> result = new PageImpl<>(paramEntities, pageRequest, count);
        System.out.println(JacksonUtil.toJson(result));
    }

    @Test
    public void testTransactionRollback(){
        SystemParamDaoImpl systemParamDao = new SystemParamDaoImpl(appContext);
        Handle handle = null;
        try {
            handle = jdbi.open();
            long total = systemParamDao.countAll();
            LOGGER.info("Total is {}.", total);
            Long id = systemParamDao.create(SystemParamEntity.builder()
                .withParamName("test_dns").withParamValue("jack007.top").withParamName("The DNS name").withParamDesc("dns name well known").build());
            LOGGER.info("Created new with id [{}].", id);
            //table not exists exception
            handle.createUpdate("update bad_table set id = 1 ;").execute();
            handle.commit();
        }catch (Throwable t){
            LOGGER.error("Error happend, rollback.", t);
            handle.rollback();
        }finally {
            if (handle != null){
                handle.close();
            }
        }
    }


    @Test
    public void testTransactionCommit(){
        SystemParamDaoImpl systemParamDao = new SystemParamDaoImpl(appContext);
        Handle handle = null;
        try {
            handle = jdbi.open();
            long total = systemParamDao.countAll();
            LOGGER.info("Total is {}.", total);
            Long id = systemParamDao.create(SystemParamEntity.builder()
                .withParamName("test_dns").withParamValue("jack007.top").withParamName("The DNS name").withParamDesc("dns name well known").build());
            LOGGER.info("Created new with id [{}].", id);
            total = systemParamDao.countAll();
            id = systemParamDao.create(SystemParamEntity.builder()
                .withParamName("test_dns").withParamValue("jack007.top").withParamName("The DNS name").withParamDesc("dns name well known").build());
            LOGGER.info("Created new with id [{}].", id);
            LOGGER.info("Total is {}.", total);
            handle.commit();
        }catch (Throwable t){
            LOGGER.error("Error happend, rollback.", t);
            handle.rollback();
        }finally {
            if (handle != null){
                handle.close();
            }
        }
    }

    private static AppContext appContext;
    private static Config config;
    private static SimpleDataSourceFactory dataSourceFactory;
    private static Jdbi jdbi;
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDataSourceFactoryTest.class);
}
