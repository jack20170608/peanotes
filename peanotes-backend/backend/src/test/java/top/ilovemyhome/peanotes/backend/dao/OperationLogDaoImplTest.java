package top.ilovemyhome.peanotes.backend.dao;

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.db.SimpleDataSourceFactory;
import top.ilovemyhome.peanotes.backend.dao.operation.OperationLogDaoImpl;
import top.ilovemyhome.peanotes.backend.application.AppContext;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OperationLogDaoImplTest {

    static AppContext appcontext ;
    static OperationLogDaoImpl operationLogDao;

    @BeforeAll
    public static void init(){
        appcontext = mock(AppContext.class);
        Jdbi jdbi = mock(Jdbi.class);

        SimpleDataSourceFactory simpleDataSourceFactory= mock(SimpleDataSourceFactory.class);
        when(appcontext.getDataSourceFactory()).thenReturn(simpleDataSourceFactory);
        when(simpleDataSourceFactory.getJdbi()).thenReturn(jdbi);
        operationLogDao = new OperationLogDaoImpl(appcontext);
    }

    @Test
    public void test(){
//        Type type = operationLogDao.getEntityType();
//        System.out.println(type);
    }
}
