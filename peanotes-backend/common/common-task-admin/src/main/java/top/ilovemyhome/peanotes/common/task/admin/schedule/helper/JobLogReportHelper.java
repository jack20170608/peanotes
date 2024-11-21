package top.ilovemyhome.peanotes.common.task.admin.schedule.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobLogReportDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobLogReport;
import top.ilovemyhome.peanotes.common.task.admin.schedule.context.JobScheduleContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * job log report helper
 *
 * @author xuxueli 2019-11-22
 */
public class JobLogReportHelper {


    public static JobLogReportHelper getInstance(JobScheduleContext jobScheduleContext) {
        if (instance == null) {
            synchronized (JobLogReportHelper.class) {
                if (instance == null) {
                    instance = new JobLogReportHelper(jobScheduleContext);
                }
            }
        }
        return instance;
    }


    private Thread logrThread;
    private volatile boolean toStop = false;

    public void start() {
        logrThread = new Thread(() -> {
            // last clean log time
            long lastCleanLogTime = 0;
            while (!toStop) {
                // 1、log-report refresh: refresh log report in 3 days
                try {
                    final LocalDate today = LocalDate.now();
                    IntStream.range(1, 4).boxed().forEach(i -> {
                        LocalDate triggerDate = today.minusDays(i);
                        LocalDateTime fromDt = triggerDate.atStartOfDay();
                        LocalDateTime toDt = fromDt.plusDays(1);

                        // refresh log-report every minute
                        JobLogReport jobLogReport = new JobLogReport();
                        jobLogReport.setTriggerDate(triggerDate);
                        jobLogReport.setRunningCount(0);
                        jobLogReport.setSucCount(0);
                        jobLogReport.setFailCount(0);
                        Map<String, Object> triggerCountMap = jobScheduleContext.getJobLogDao().findLogReport(fromDt, toDt);
                        if (triggerCountMap != null && !triggerCountMap.isEmpty()) {
                            int triggerDayCount = triggerCountMap.containsKey("triggerDayCount") ? Integer.valueOf(String.valueOf(triggerCountMap.get("triggerDayCount"))) : -1;
                            int triggerDayCountRunning = triggerCountMap.containsKey("triggerDayCountRunning") ? Integer.valueOf(String.valueOf(triggerCountMap.get("triggerDayCountRunning"))) : -1;
                            int triggerDayCountSuc = triggerCountMap.containsKey("triggerDayCountSuc") ? Integer.valueOf(String.valueOf(triggerCountMap.get("triggerDayCountSuc"))) : -1;
                            int triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;

                            jobLogReport.setRunningCount(triggerDayCountRunning);
                            jobLogReport.setSucCount(triggerDayCountSuc);
                            jobLogReport.setFailCount(triggerDayCountFail);
                        }
                        JobLogReportDao jobLogReportDao = jobScheduleContext.getJobLogReportDao();
                        // do refresh
                        int ret = jobLogReportDao.updateByTriggerDate(triggerDate, jobLogReport);
                        if (ret <= 0) {
                            jobLogReportDao.create(jobLogReport);
                        }
                    });
                } catch (Exception e) {
                    if (!toStop) {
                        LOGGER.error(">>>>>>>>>>> xxl-job, job log report thread error:{}", e);
                    }
                }

                // 2、log-clean: switch open & once each day
//                if (XxlJobAdminConfig.getAdminConfig().getLogretentiondays() > 0
//                    && System.currentTimeMillis() - lastCleanLogTime > 24 * 60 * 60 * 1000) {
//
//                    // expire-time
//                    Calendar expiredDay = Calendar.getInstance();
//                    expiredDay.add(Calendar.DAY_OF_MONTH, -1 * XxlJobAdminConfig.getAdminConfig().getLogretentiondays());
//                    expiredDay.set(Calendar.HOUR_OF_DAY, 0);
//                    expiredDay.set(Calendar.MINUTE, 0);
//                    expiredDay.set(Calendar.SECOND, 0);
//                    expiredDay.set(Calendar.MILLISECOND, 0);
//                    Date clearBeforeTime = expiredDay.getTime();
//
//                    // clean expired log
//                    List<Long> logIds = null;
//                    do {
//                        logIds = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().findClearLogIds(0, 0, clearBeforeTime, 0, 1000);
//                        if (logIds != null && logIds.size() > 0) {
//                            XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().clearLog(logIds);
//                        }
//                    } while (logIds != null && logIds.size() > 0);
//
//                    // update clean time
//                    lastCleanLogTime = System.currentTimeMillis();
//                }

                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (Exception e) {
                    if (!toStop) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }

            }

            LOGGER.info(">>>>>>>>>>> xxl-job, job log report thread stop");

        });
        logrThread.setDaemon(true);
        logrThread.setName("xxl-job, admin JobLogReportHelper");
        logrThread.start();
    }

    public void stop() {
        toStop = true;
        // interrupt and wait
        logrThread.interrupt();
        try {
            logrThread.join();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    private final static Logger LOGGER = LoggerFactory.getLogger(JobLogReportHelper.class);

    private volatile static JobLogReportHelper instance;

    private JobLogReportHelper(JobScheduleContext jobScheduleContext) {
        this.jobScheduleContext = jobScheduleContext;
    }

    private final JobScheduleContext jobScheduleContext;
}
