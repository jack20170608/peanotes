package top.ilovemyhome.peanotes.common.task.admin.schedule.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.admin.application.AppContext;
import top.ilovemyhome.peanotes.common.task.admin.dao.*;
import top.ilovemyhome.peanotes.common.task.admin.schedule.JobScheduler;
import top.ilovemyhome.peanotes.common.task.admin.schedule.alarm.JobAlarm;
import top.ilovemyhome.peanotes.common.task.admin.schedule.completer.JobCompleter;

import javax.sql.DataSource;

public class JobScheduleContext {


    public JobScheduleContext(AppContext appContext) {
        this.jobInfoDao = appContext.getBean("jobInfoDao", JobInfoDao.class);
        this.jobGroupDao = appContext.getBean("jobGroupDao", JobGroupDao.class);
        this.jobLogDao = appContext.getBean("jobLogDao", JobLogDao.class);
        this.jobLogGlueDao = appContext.getBean("jobLogGlueDao", JobLogGlueDao.class);
        this.jobRegistryDao = appContext.getBean("jobRegistryDao", JobRegistryDao.class);
        this.jobLogReportDao = appContext.getBean("jobLogReportDao", JobLogReportDao.class);
        this.dataSource = appContext.getDataSourceFactory().getHikariDataSource();
        this.triggerPoolFastMax = appContext.getConfig().getInt("schedule.fast_trigger_pool.max_thread_num");
        this.triggerPoolSlowMax = appContext.getConfig().getInt("schedule.slow_trigger_pool.max_thread_num");
        this.jobAlarm = null;
        this.jobScheduler = new JobScheduler(this);
        this.jobCompleter = new JobCompleter(this);
        this.accessToken = "foo";

    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public JobAlarm getJobAlarm() {
        return jobAlarm;
    }

    public JobInfoDao getJobInfoDao() {
        return jobInfoDao;
    }

    public JobGroupDao getJobGroupDao() {
        return jobGroupDao;
    }

    public JobLogGlueDao getJobLogGlueDao() {
        return jobLogGlueDao;
    }

    public JobLogDao getJobLogDao() {
        return jobLogDao;
    }

    public JobRegistryDao getJobRegistryDao() {
        return jobRegistryDao;
    }

    public JobLogReportDao getJobLogReportDao() {
        return jobLogReportDao;
    }

    public int getTriggerPoolFastMax() {
        return triggerPoolFastMax;
    }

    public int getTriggerPoolSlowMax() {
        return triggerPoolSlowMax;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public JobScheduler getJobScheduler() {
        return jobScheduler;
    }

    public JobCompleter getJobCompleter() {
        return jobCompleter;
    }

    private final DataSource dataSource;

    private final String accessToken;
    private final int triggerPoolFastMax;
    private final int triggerPoolSlowMax;

    private final JobInfoDao jobInfoDao;
    private final JobGroupDao jobGroupDao;
    private final JobLogGlueDao jobLogGlueDao;
    private final JobLogDao jobLogDao;
    private final JobRegistryDao jobRegistryDao;
    private final JobLogReportDao jobLogReportDao;

    private final JobAlarm jobAlarm;
    private final JobScheduler jobScheduler;
    private final JobCompleter jobCompleter;


    private final Logger LOGGER = LoggerFactory.getLogger(JobScheduleContext.class);

}
