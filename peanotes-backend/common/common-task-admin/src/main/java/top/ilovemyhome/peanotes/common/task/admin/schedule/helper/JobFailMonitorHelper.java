package top.ilovemyhome.peanotes.common.task.admin.schedule.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.TriggerTypeEnum;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobInfoDao;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobLogDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobInfo;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobLog;
import top.ilovemyhome.peanotes.common.task.admin.schedule.context.JobScheduleContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * job monitor instance
 *
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobFailMonitorHelper {


    public static JobFailMonitorHelper getInstance(JobScheduleContext jobScheduleContext) {
        if (INSTANCE == null) {
            synchronized (JobScheduleContext.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JobFailMonitorHelper(jobScheduleContext);
                }
            }
        }
        return INSTANCE;
    }

    // ---------------------- monitor ----------------------

    private Thread monitorThread;
    private volatile boolean toStop = false;

    public void start() {
        monitorThread = new Thread(new Runnable() {

            @Override
            public void run() {

                // monitor
                while (!toStop) {
                    try {
                        JobLogDao jobLogDao = jobScheduleContext.getJobLogDao();
                        JobInfoDao jobInfoDao = jobScheduleContext.getJobInfoDao();
                        List<Long> failLogIds = jobLogDao.findFailJobLogIds(1000L);
                        if (failLogIds != null && !failLogIds.isEmpty()) {
                            for (long failLogId : failLogIds) {
                                // lock log
                                int lockRet = jobLogDao.updateAlarmStatus(failLogId, 0, -1);
                                if (lockRet < 1) {
                                    continue;
                                }
                                JobLog log = jobLogDao.findOne(failLogId).orElse(null);
                                if (log == null) {
                                    continue;
                                }
                                JobInfo info = jobInfoDao.findOne(log.getJobId()).orElse(null);

                                // 1、fail retry monitor
                                if (log.getExecutorFailRetryCount() > 0) {
                                    JobTriggerPoolHelper.trigger(log.getJobId(), TriggerTypeEnum.RETRY, (log.getExecutorFailRetryCount() - 1), log.getExecutorShardingParam(), log.getExecutorParam(), null);
                                    String retryMsg = "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>Retry triggered.<<<<<<<<<<< </span><br>";
                                    log.setTriggerMsg(log.getTriggerMsg() + retryMsg);
                                    jobLogDao.update(log.getJobId(), log);
                                }

                                // 2、fail alarm monitor
                                int newAlarmStatus = 0;        // 告警状态：0-默认、-1=锁定状态、1-无需告警、2-告警成功、3-告警失败
                                if (info != null) {
                                    boolean alarmResult = jobScheduleContext.getJobAlarm().alarm(info, log);
                                    newAlarmStatus = alarmResult ? 2 : 3;
                                } else {
                                    newAlarmStatus = 1;
                                }

                                jobLogDao.updateAlarmStatus(failLogId, -1, newAlarmStatus);
                            }
                        }

                    } catch (Exception e) {
                        if (!toStop) {
                            LOGGER.error(">>>>>>>>>>> xxl-job, job fail monitor thread error:{}", e);
                        }
                    }

                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (Exception e) {
                        if (!toStop) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }

                }

                LOGGER.info(">>>>>>>>>>> xxl-job, job fail monitor thread stop");

            }
        });
        monitorThread.setDaemon(true);
        monitorThread.setName("xxl-job, admin JobFailMonitorHelper");
        monitorThread.start();
    }

    public void stop() {
        toStop = true;
        // interrupt and wait
        monitorThread.interrupt();
        try {
            monitorThread.join();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    private static Logger LOGGER = LoggerFactory.getLogger(JobFailMonitorHelper.class);

    private static volatile JobFailMonitorHelper INSTANCE;

    private JobFailMonitorHelper(JobScheduleContext jobScheduleContext) {
        this.jobScheduleContext = jobScheduleContext;
    }

    private final JobScheduleContext jobScheduleContext;


}
