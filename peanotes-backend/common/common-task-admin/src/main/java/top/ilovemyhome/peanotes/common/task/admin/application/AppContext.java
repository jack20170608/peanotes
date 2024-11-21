package top.ilovemyhome.peanotes.common.task.admin.application;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.db.FlyWayHelper;
import top.ilovemyhome.peanotes.backend.common.db.SimpleDataSourceFactory;
import top.ilovemyhome.peanotes.common.task.admin.dao.*;
import top.ilovemyhome.peanotes.common.task.admin.dao.impl.*;
import top.ilovemyhome.peanotes.common.task.admin.service.JobService;
import top.ilovemyhome.peanotes.common.task.admin.service.impl.JobServiceImpl;

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

    private void initDao() {
        JobGroupDao jobGroupDao = new JobGroupDaoImpl(this);
        BEAN_FACTORY.put(JobGroupDao.class, jobGroupDao);
        BEAN_NAME_FACTORY.put("jobGroupDao", jobGroupDao);

        JobInfoDao jobInfoDao = new JobInfoDaoImpl(this);
        BEAN_FACTORY.put(JobInfoDao.class, jobInfoDao);
        BEAN_NAME_FACTORY.put("jobInfoDao", jobInfoDao);

        JobLogDaoImpl jobLogDao = new JobLogDaoImpl(this);
        BEAN_FACTORY.put(JobLogDao.class, jobLogDao);
        BEAN_NAME_FACTORY.put("jobLogDao", jobLogDao);

        JobLogReportDao jobLogReportDao = new JobLogReportDaoImpl(this);
        BEAN_FACTORY.put(JobLogReportDao.class, jobLogReportDao);
        BEAN_NAME_FACTORY.put("jobLogReportDao", jobLogReportDao);

        JobLogGlueDao jobLogGlueDao = new JobLogGlueDaoImpl(this);
        BEAN_FACTORY.put(JobLogGlueDao.class, jobLogGlueDao);
        BEAN_NAME_FACTORY.put("jobLogGlueDao", jobLogGlueDao);

        JobRegistryDao jobRegistryDao = new JobRegistryDaoImpl(this);
        BEAN_FACTORY.put(JobRegistryDao.class, jobRegistryDao);
        BEAN_NAME_FACTORY.put("jobRegistryDao", jobRegistryDao);

        JobUserDao jobUserDao = new JobUserDaoImpl(this);
        BEAN_FACTORY.put(JobUserDao.class, jobUserDao);
        BEAN_NAME_FACTORY.put("JobUserDao", jobUserDao);

    }

    private void initService() {
        JobService jobService = new JobServiceImpl(this);
        BEAN_FACTORY.put(JobService.class, jobService);
        BEAN_NAME_FACTORY.put("jobService", jobService);
    }

    public String getApplicationName() {
        return config.getString("name");
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName, Class<T> beanClass) {
        return (T) BEAN_FACTORY.getOrDefault(beanClass, (T) BEAN_NAME_FACTORY.get(beanName));
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
