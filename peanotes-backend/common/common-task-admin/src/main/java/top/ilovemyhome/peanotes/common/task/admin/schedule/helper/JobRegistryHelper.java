package top.ilovemyhome.peanotes.common.task.admin.schedule.helper;


import org.flywaydb.core.internal.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.admin.core.config.RegistryConfig;
import top.ilovemyhome.peanotes.common.task.admin.core.model.RegistryParam;
import top.ilovemyhome.peanotes.common.task.admin.core.model.ReturnT;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobGroupDao;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobRegistryDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobGroup;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobRegistry;
import top.ilovemyhome.peanotes.common.task.admin.schedule.context.JobScheduleContext;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * job registry instance
 *
 * @author xuxueli 2016-10-02 19:10:24
 */
public class JobRegistryHelper {


    public static JobRegistryHelper getInstance(JobScheduleContext jobScheduleContext) {
        if (instance == null) {
            synchronized (JobRegistryHelper.class) {
                if (instance == null) {
                    instance = new JobRegistryHelper(jobScheduleContext);
                }
            }
        }
        return instance;
    }

    public void start() {

        // for registry or remove
        registryOrRemoveThreadPool = new ThreadPoolExecutor(
            2,
            10,
            30L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(2000),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "xxl-job, admin JobRegistryMonitorHelper-registryOrRemoveThreadPool-" + r.hashCode());
                }
            },
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    r.run();
                    LOGGER.warn(">>>>>>>>>>> xxl-job, registry or remove too fast, match threadpool rejected handler(run now).");
                }
            });

        // for monitor
        registryMonitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        final JobGroupDao jobGroupDao = jobScheduleContext.getJobGroupDao();
                        final JobRegistryDao jobRegistryDao = jobScheduleContext.getJobRegistryDao();
                        // auto registry group
                        List<JobGroup> groupList = jobGroupDao.findByAddressType(0);
                        if (groupList != null && !groupList.isEmpty()) {

                            // remove dead address (admin/executor)
                            List<Long> ids = jobRegistryDao.findDead(LocalDateTime.now().minusSeconds(RegistryConfig.DEAD_TIMEOUT));
                            if (ids != null && ids.size() > 0) {
                                jobRegistryDao.delete(ids);
                            }

                            // fresh online address (admin/executor)
                            HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
                            List<JobRegistry> list = jobRegistryDao.findAll(LocalDateTime.now().minusSeconds(RegistryConfig.DEAD_TIMEOUT));
                            if (list != null) {
                                for (JobRegistry item : list) {
                                    if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                                        String appname = item.getRegistryKey();
                                        List<String> registryList = appAddressMap.get(appname);
                                        if (registryList == null) {
                                            registryList = new ArrayList<String>();
                                        }

                                        if (!registryList.contains(item.getRegistryValue())) {
                                            registryList.add(item.getRegistryValue());
                                        }
                                        appAddressMap.put(appname, registryList);
                                    }
                                }
                            }

                            // fresh group address
                            for (JobGroup group : groupList) {
                                List<String> registryList = appAddressMap.get(group.getAppName());
                                String addressListStr = null;
                                if (registryList != null && !registryList.isEmpty()) {
                                    Collections.sort(registryList);
                                    StringBuilder addressListSB = new StringBuilder();
                                    for (String item : registryList) {
                                        addressListSB.append(item).append(",");
                                    }
                                    addressListStr = addressListSB.toString();
                                    addressListStr = addressListStr.substring(0, addressListStr.length() - 1);
                                }
                                group.setAddressList(addressListStr);
                                group.setUpdateTime(LocalDateTime.now());
                                jobScheduleContext.getJobGroupDao().update(group.getId(), group);
                            }
                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            LOGGER.error(">>>>>>>>>>> xxl-job, job registry monitor thread error:{}", e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                    } catch (InterruptedException e) {
                        if (!toStop) {
                            LOGGER.error(">>>>>>>>>>> xxl-job, job registry monitor thread error:{}", e);
                        }
                    }
                }
                LOGGER.info(">>>>>>>>>>> xxl-job, job registry monitor thread stop");
            }
        });
        registryMonitorThread.setDaemon(true);
        registryMonitorThread.setName("xxl-job, admin JobRegistryMonitorHelper-registryMonitorThread");
        registryMonitorThread.start();
    }

    public void stop() {
        toStop = true;

        // stop registryOrRemoveThreadPool
        registryOrRemoveThreadPool.shutdownNow();

        // stop monitir (interrupt and wait)
        registryMonitorThread.interrupt();
        try {
            registryMonitorThread.join();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    // ---------------------- helper ----------------------

    public ReturnT<String> registry(RegistryParam registryParam) {

        // valid
        if (!StringUtils.hasText(registryParam.getRegistryGroup())
            || !StringUtils.hasText(registryParam.getRegistryKey())
            || !StringUtils.hasText(registryParam.getRegistryValue())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Illegal Argument.");
        }

        // async execute
        registryOrRemoveThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                JobRegistryDao jobRegistryDao = jobScheduleContext.getJobRegistryDao();
                int ret = jobRegistryDao.registryUpdate(LocalDateTime.now(), registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
                if (ret < 1) {
                    jobRegistryDao.registrySave(LocalDateTime.now(), registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
                    // fresh
                    freshGroupRegistryInfo(registryParam);
                }
            }
        });

        return ReturnT.SUCCESS;
    }

    public ReturnT<String> registryRemove(RegistryParam registryParam) {

        // valid
        if (!StringUtils.hasText(registryParam.getRegistryGroup())
            || !StringUtils.hasText(registryParam.getRegistryKey())
            || !StringUtils.hasText(registryParam.getRegistryValue())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Illegal Argument.");
        }

        // async execute
        registryOrRemoveThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                JobRegistryDao jobRegistryDao = jobScheduleContext.getJobRegistryDao();
                int ret = jobRegistryDao.registryDelete(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
                if (ret > 0) {
                    // fresh
                    freshGroupRegistryInfo(registryParam);
                }
            }
        });

        return ReturnT.SUCCESS;
    }

    private void freshGroupRegistryInfo(RegistryParam registryParam) {
        // Under consideration, prevent affecting core tables
    }

    private ThreadPoolExecutor registryOrRemoveThreadPool = null;
    private Thread registryMonitorThread;
    private volatile boolean toStop = false;


    private static Logger LOGGER = LoggerFactory.getLogger(JobRegistryHelper.class);

    private static JobRegistryHelper instance;

    private JobRegistryHelper(JobScheduleContext jobScheduleContext) {
        this.jobScheduleContext = jobScheduleContext;
    }

    private final JobScheduleContext jobScheduleContext;

}
