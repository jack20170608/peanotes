package top.ilovemyhome.peanotes.backend.application;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.db.FlyWayHelper;
import top.ilovemyhome.peanotes.backend.common.db.SimpleDataSourceFactory;
import top.ilovemyhome.peanotes.backend.dao.QueryDao;
import top.ilovemyhome.peanotes.backend.dao.QueryDaoImpl;
import top.ilovemyhome.peanotes.backend.service.QueryService;
import top.ilovemyhome.peanotes.backend.service.QueryServiceImpl;

import java.util.HashMap;
import java.util.Map;


public class AppContext {

    public AppContext(String env, Config config) {
        this.env = env;
        this.config = config;
    }

    private SimpleDataSourceFactory dataSourceFactory = null;


    public synchronized void init() {
        LOGGER.info("Init the application context..........");
        dataSourceFactory = SimpleDataSourceFactory.getInstance(this.config);
        //1.dao
        initDao();

        //2. service
        initService();

    }

    private void initDao() {
        QueryDao queryDao = new QueryDaoImpl(this);
        BEAN_FACTORY.put(QueryDao.class, queryDao);
        BEAN_NAME_FACTORY.put("QueryDao", queryDao);
    }

    private void initService() {
        QueryService queryService = new QueryServiceImpl(this);
        BEAN_FACTORY.put(QueryService.class, queryService);
        BEAN_NAME_FACTORY.put("queryService", queryService);
    }

    public String getApplicationName() {
        return config.getString("name");
    }

    public String getEnv() {
        return env;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName, Class<T> beanClass) {
        return (T) BEAN_FACTORY.getOrDefault(beanClass, (T) BEAN_NAME_FACTORY.get(beanName));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AppContext.class);

    private final String env;
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
