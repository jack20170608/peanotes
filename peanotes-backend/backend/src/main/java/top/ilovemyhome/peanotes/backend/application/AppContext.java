package top.ilovemyhome.peanotes.backend.application;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.db.FlyWayHelper;
import top.ilovemyhome.peanotes.backend.common.db.SimpleDataSourceFactory;
import top.ilovemyhome.peanotes.backend.dao.operation.OperationLogDaoImpl;
import top.ilovemyhome.peanotes.backend.dao.system.SystemParamDaoImpl;
import top.ilovemyhome.peanotes.backend.service.operation.OperationLogCrudService;
import top.ilovemyhome.peanotes.backend.service.system.SystemParamCrudService;

import java.util.HashMap;
import java.util.Map;


public class AppContext {

    public AppContext(Config config) {
        this.config = config;
    }

    private SimpleDataSourceFactory dataSourceFactory = null;


    public synchronized void init() {
        LOGGER.info("Init the application context..........");
        //0. the infrastructure
        FlyWayHelper.run(this.config);
        dataSourceFactory = SimpleDataSourceFactory.getInstance(this.config);

        //1.dao
        initDao();

        //2. service
        initService();

    }

    private void initDao(){
        OperationLogDaoImpl operationLogDao = new OperationLogDaoImpl(this);
        BEAN_FACTORY.put(OperationLogDaoImpl.class, operationLogDao);
        BEAN_NAME_FACTORY.put("operationLogDao", operationLogDao);

        SystemParamDaoImpl systemParamDao = new SystemParamDaoImpl(this);
        BEAN_FACTORY.put(SystemParamDaoImpl.class, systemParamDao);
        BEAN_NAME_FACTORY.put("systemParamDao", systemParamDao);
    }

    private void initService(){
        OperationLogCrudService operationLogCrudService = new OperationLogCrudService(this);
        BEAN_FACTORY.put(OperationLogCrudService.class, operationLogCrudService);
        BEAN_NAME_FACTORY.put("operationLogCrudService", operationLogCrudService);

        SystemParamCrudService systemParamCrudService = new SystemParamCrudService(this);
        BEAN_FACTORY.put(SystemParamCrudService.class, systemParamCrudService);
        BEAN_NAME_FACTORY.put("systemParamCrudService", systemParamCrudService);
    }

    public String getApplicationName() {
        return config.getString("name");
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName, Class<T> beanClass) {
        return (T)BEAN_FACTORY.getOrDefault(beanClass, (T)BEAN_NAME_FACTORY.get(beanName));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AppContext.class);

    private final Config config;

    private final Map<Class<?>, Object> BEAN_FACTORY = new HashMap<>();
    private final Map<String, Object> BEAN_NAME_FACTORY = new HashMap<>();

    public Config getConfig() {
        return config;
    }

    public SimpleDataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }
}
