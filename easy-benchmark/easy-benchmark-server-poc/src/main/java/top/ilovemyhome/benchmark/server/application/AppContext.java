package top.ilovemyhome.benchmark.server.application;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.benchmark.server.config.UserConfig;
import top.ilovemyhome.benchmark.server.dao.BenchmarkTestCaseDao;
import top.ilovemyhome.benchmark.server.dao.BenchmarkTestResultDao;
import top.ilovemyhome.benchmark.server.dao.impl.BenchmarkTestCaseDaoJdbiImpl;
import top.ilovemyhome.benchmark.server.dao.impl.BenchmarkTestResultDaoImpl;
import top.ilovemyhome.benchmark.server.eventbus.MessageListener;
import top.ilovemyhome.commons.common.config.ConfigLoader;
import top.ilovemyhome.commons.database.flyway.FlyWayHelper;
import top.ilovemyhome.commons.database.pool.HikariDataSourceFactory;
import top.ilovemyhome.commons.muserver.security.AppSecurityContext;
import top.ilovemyhome.commons.muserver.security.core.CookieValueType;
import top.ilovemyhome.commons.muserver.security.core.User;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;

public class AppContext {

    public static AppContext getInstance() {
        if (null == instance) {
            synchronized (AppContext.class) {
                if (null == instance) {
                    instance = new AppContext();
                    instance.init();
                }
            }
        }
        return instance;
    }

    private void init() {
        env = System.getProperty("env", "dev");
        config = ConfigLoader.loadConfigByEnv(env);
        appName = config.getString("application.name");
        initTheDataSourceFactory(config);
        initTheThreadPool();
        initTheEventBus();
        initSecurity();

        //Init the DAO layer
        initDaoLayer();
        //Run the flyway migration
        FlyWayHelper.run(config);
    }

    private void initDaoLayer() {
        logger.info("Init the dao layer..........");
        //Register the dao layer
        registerBean(new BenchmarkTestCaseDaoJdbiImpl(this)
            , "benchmarkTestCaseDao", BenchmarkTestCaseDao.class);
        registerBean(new BenchmarkTestResultDaoImpl(this), "benchmarkTestResultDao", BenchmarkTestResultDao.class);
    }

    private void initTheDataSourceFactory(Config config) {
        logger.info("Init the data source factory..........");
        hikariDataSourceFactory = HikariDataSourceFactory.getInstance(config);
    }

    private void initTheThreadPool() {
        int nThreads = Runtime.getRuntime().availableProcessors();
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("thread-pool-%d")
            .build();
        threadPool = new ThreadPoolExecutor(nThreads, 20, 0L, TimeUnit.MILLISECONDS
            , new ArrayBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    private void initTheEventBus() {
        eventBus = new EventBus();
        MessageListener messageListener = new MessageListener(this);
        eventBus.register(messageListener);
        registerBean(messageListener, "messageListener", MessageListener.class);
    }

    private void initSecurity() {
        logger.info("Init the security..........");
        List<User> users = ConfigLoader.loadConfigAsBeanList(config, "server.security.users", UserConfig.class)
            .stream()
            .map(UserConfig::toUser)
            .toList();
        AppSecurityContext appSecurityContext = AppSecurityContext.builder()
            .inMemoryUser(users)
            .jwtIssuer(this.appName)
            .jwtSubject("access")
            .jwtTtl(config.getDuration("server.security.jwt.ttl", TimeUnit.MILLISECONDS))
            .jwtPublicKeyPath(config.getString("server.security.jwt.publicKeyLocation"))
            .jwtPrivateKeyPath(config.getString("server.security.jwt.privateKeyLocation"))
            .cookieName(config.getString("server.security.cookie.name"))
            .cookieValueType(config.getEnum(CookieValueType.class, "server.security.cookie.valueType"))
            .build();
        registerBean(appSecurityContext, "appSecurityContext", AppSecurityContext.class);
    }

    public <T> T getBean(String beanName, Class<T> clazz) {
        return (T) beanNameMap.getOrDefault(beanName, (T) beanMap.get(clazz));
    }

    public <T> void registerBean(T bean, String beanName, Class<T> clazz) {
        beanNameMap.put(beanName, bean);
        beanMap.put(clazz, bean);
        if (bean instanceof AutoCloseable) {
            closeableBeanMap.put(beanName, bean);
        }
    }

    public String getEnv() {
        return env;
    }

    public Config getConfig() {
        return config;
    }

    public HikariDataSourceFactory getHikariDataSourceFactory() {
        return hikariDataSourceFactory;
    }

    private String env;
    private String appName;
    private Config config;
    private HikariDataSourceFactory hikariDataSourceFactory;
    private ExecutorService threadPool;
    private EventBus eventBus;

    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();
    private final Map<String, Object> beanNameMap = new ConcurrentHashMap<>();
    private final TreeMap<String, Object> closeableBeanMap = new TreeMap<>();

    private static AppContext instance;
    private static final Logger logger = LoggerFactory.getLogger(AppContext.class);


}
