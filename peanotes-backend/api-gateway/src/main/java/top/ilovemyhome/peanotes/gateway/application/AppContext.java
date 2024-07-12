package top.ilovemyhome.peanotes.gateway.application;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.db.SimpleDataSourceFactory;

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
        dataSourceFactory = SimpleDataSourceFactory.getInstance(this.config);

        //1.dao
        initDao();

        //2. service
        initService();

    }

    private void initDao(){

    }

    private void initService(){

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
