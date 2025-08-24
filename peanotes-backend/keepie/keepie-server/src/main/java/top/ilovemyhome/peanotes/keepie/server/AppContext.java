package top.ilovemyhome.peanotes.keepie.server;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.db.SimpleDataSourceFactory;
import top.ilovemyhome.peanotes.keepie.server.service.KeepieService;
import top.ilovemyhome.peanotes.keepie.server.service.KeepieServiceImpl;

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
        //2. service
        initService();

    }



    private void initService() {
        KeepieService keepieService = new KeepieServiceImpl(this);
        BEAN_FACTORY.put(KeepieService.class, keepieService);
        BEAN_NAME_FACTORY.put("keepieService", keepieService);
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
