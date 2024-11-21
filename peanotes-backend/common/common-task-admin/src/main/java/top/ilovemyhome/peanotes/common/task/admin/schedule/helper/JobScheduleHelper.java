package top.ilovemyhome.peanotes.common.task.admin.schedule.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.admin.core.cron.CronExpression;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.MisfireStrategyEnum;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.ScheduleType;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.TriggerTypeEnum;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobInfo;
import top.ilovemyhome.peanotes.common.task.admin.schedule.context.JobScheduleContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static top.ilovemyhome.peanotes.backend.common.utils.StringConvertUtils.toInt;

/**
 * @author xuxueli 2019-05-21
 */
public class JobScheduleHelper {

    public static JobScheduleHelper getInstance(JobScheduleContext jobScheduleContext) {
        if (instance == null) {
            synchronized (JobScheduleHelper.class) {
                if (instance == null) {
                    instance = new JobScheduleHelper(jobScheduleContext);
                }
            }
        }
        return instance;
    }

    public static final long PRE_READ_MS = 5000;    // pre read

    private Thread scheduleThread;
    private Thread ringThread;
    private volatile boolean scheduleThreadToStop = false;
    private volatile boolean ringThreadToStop = false;
    private volatile static Map<Integer, List<Long>> ringData = new ConcurrentHashMap<>();

    public void start() {

        // schedule thread
        scheduleThread = new Thread(() -> {

            try {
                TimeUnit.MILLISECONDS.sleep(5000 - System.currentTimeMillis() % 1000);
            } catch (InterruptedException e) {
                if (!scheduleThreadToStop) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            LOGGER.info(">>>>>>>>> init xxl-job admin scheduler success.");

            // pre-read count: treadpool-size * trigger-qps (each trigger cost 50ms, qps = 1000/50 = 20)
            int preReadCount = (jobScheduleContext.getTriggerPoolFastMax() + jobScheduleContext.getTriggerPoolSlowMax()) * 20;

            while (!scheduleThreadToStop) {

                // Scan Job
                long start = System.currentTimeMillis();

                Connection conn = null;
                Boolean connAutoCommit = null;
                PreparedStatement preparedStatement = null;

                boolean preReadSuc = true;
                try {

                    conn = jobScheduleContext.getDataSource().getConnection();
                    connAutoCommit = conn.getAutoCommit();
                    conn.setAutoCommit(false);

                    preparedStatement = conn.prepareStatement("select * from t_job_lock where lock_name = 'schedule_lock' for update");
                    preparedStatement.execute();

                    // tx start

                    // 1、pre read
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime preReadTime = now.plusSeconds(PRE_READ_MS);
                    List<JobInfo> scheduleList = jobScheduleContext.getJobInfoDao().scheduleJobQuery(preReadTime, preReadCount);
                    if (scheduleList != null && scheduleList.size() > 0) {
                        // 2、push time-ring
                        for (JobInfo jobInfo : scheduleList) {

                            // time-ring jump
                            if (now.isAfter(jobInfo.getTriggerNextTime().plusSeconds(PRE_READ_MS))) {
                                // 2.1、trigger-expire > 5s：pass && make next-trigger-time
                                LOGGER.warn(">>>>>>>>>>> xxl-job, schedule misfire, jobId = " + jobInfo.getId());

                                // 1、misfire match
                                MisfireStrategyEnum misfireStrategyEnum =jobInfo.getMisfireStrategy();
                                if (MisfireStrategyEnum.FIRE_ONCE_NOW == misfireStrategyEnum) {
                                    // FIRE_ONCE_NOW 》 trigger
                                    JobTriggerPoolHelper.trigger(jobInfo.getId(), TriggerTypeEnum.MISFIRE, -1, null, null, null);
                                    LOGGER.debug(">>>>>>>>>>> xxl-job, schedule push trigger : jobId = " + jobInfo.getId());
                                }

                                // 2、fresh next
                                refreshNextValidTime(jobInfo, LocalDateTime.now());

                            } else if (now.isAfter(jobInfo.getTriggerNextTime())) {
                                // 2.2、trigger-expire < 5s：direct-trigger && make next-trigger-time

                                // 1、trigger
                                JobTriggerPoolHelper.trigger(jobInfo.getId(), TriggerTypeEnum.CRON, -1, null, null, null);
                                LOGGER.debug(">>>>>>>>>>> xxl-job, schedule push trigger : jobId = " + jobInfo.getId());

                                // 2、fresh next
                                refreshNextValidTime(jobInfo, LocalDateTime.now());

                                // next-trigger-time in 5s, pre-read again
                                if (jobInfo.getTriggerStatus() == 1 && preReadTime.isAfter(jobInfo.getTriggerNextTime())) {
                                    // 1、make ring second
                                    int ringSecond = jobInfo.getTriggerNextTime().truncatedTo(ChronoUnit.SECONDS).getSecond();
                                    // 2、push time ring
                                    pushTimeRing(ringSecond, jobInfo.getId());

                                    // 3、fresh next
                                    refreshNextValidTime(jobInfo, jobInfo.getTriggerNextTime());

                                }

                            } else {
                                // 2.3、trigger-pre-read：time-ring trigger && make next-trigger-time

                                // 1、make ring second
                                int ringSecond = jobInfo.getTriggerNextTime().truncatedTo(ChronoUnit.SECONDS).getSecond();

                                // 2、push time ring
                                pushTimeRing(ringSecond, jobInfo.getId());

                                // 3、fresh next
                                refreshNextValidTime(jobInfo, jobInfo.getTriggerNextTime());

                            }

                        }

                        // 3、update trigger info
                        for (JobInfo jobInfo : scheduleList) {
                            jobScheduleContext.getJobInfoDao().scheduleUpdate(jobInfo);
                        }

                    } else {
                        preReadSuc = false;
                    }

                    // tx stop


                } catch (Exception e) {
                    if (!scheduleThreadToStop) {
                        LOGGER.error(">>>>>>>>>>> xxl-job, JobScheduleHelper#scheduleThread error:{}", e);
                    }
                } finally {

                    // commit
                    if (conn != null) {
                        try {
                            conn.commit();
                        } catch (SQLException e) {
                            if (!scheduleThreadToStop) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        }
                        try {
                            conn.setAutoCommit(connAutoCommit);
                        } catch (SQLException e) {
                            if (!scheduleThreadToStop) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        }
                        try {
                            conn.close();
                        } catch (SQLException e) {
                            if (!scheduleThreadToStop) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        }
                    }

                    // close PreparedStatement
                    if (null != preparedStatement) {
                        try {
                            preparedStatement.close();
                        } catch (SQLException e) {
                            if (!scheduleThreadToStop) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        }
                    }
                }
                long cost = System.currentTimeMillis() - start;


                // Wait seconds, align second
                if (cost < 1000) {  // scan-overtime, not wait
                    try {
                        // pre-read period: success > scan each second; fail > skip this period;
                        TimeUnit.MILLISECONDS.sleep((preReadSuc ? 1000 : PRE_READ_MS) - System.currentTimeMillis() % 1000);
                    } catch (InterruptedException e) {
                        if (!scheduleThreadToStop) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                }
            }
            LOGGER.info(">>>>>>>>>>> xxl-job, JobScheduleHelper#scheduleThread stop");
        });
        scheduleThread.setDaemon(true);
        scheduleThread.setName("xxl-job, admin JobScheduleHelper#scheduleThread");
        scheduleThread.start();


        // ring thread
        ringThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!ringThreadToStop) {

                    // align second
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis() % 1000);
                    } catch (InterruptedException e) {
                        if (!ringThreadToStop) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }

                    try {
                        // second data
                        List<Long> ringItemData = new ArrayList<>();
                        int nowSecond = Calendar.getInstance().get(Calendar.SECOND);   // 避免处理耗时太长，跨过刻度，向前校验一个刻度；

                        for (int i = 0; i < 2; i++) {
                            List<Long> tmpData = ringData.remove((nowSecond + 60 - i) % 60);
                            if (tmpData != null) {
                                ringItemData.addAll(tmpData);
                            }
                        }

                        // ring trigger
                        LOGGER.debug(">>>>>>>>>>> xxl-job, time-ring beat : " + nowSecond + " = " + Arrays.asList(ringItemData));
                        if (ringItemData.size() > 0) {
                            // do trigger
                            for (Long jobId : ringItemData) {
                                // do trigger
                                JobTriggerPoolHelper.trigger(jobId, TriggerTypeEnum.CRON, -1, null, null, null);
                            }
                            // clear
                            ringItemData.clear();
                        }
                    } catch (Exception e) {
                        if (!ringThreadToStop) {
                            LOGGER.error(">>>>>>>>>>> xxl-job, JobScheduleHelper#ringThread error:{}", e);
                        }
                    }
                }
                LOGGER.info(">>>>>>>>>>> xxl-job, JobScheduleHelper#ringThread stop");
            }
        });
        ringThread.setDaemon(true);
        ringThread.setName("xxl-job, admin JobScheduleHelper#ringThread");
        ringThread.start();
    }

    private void refreshNextValidTime(JobInfo jobInfo, LocalDateTime fromTime) throws Exception {
        LocalDateTime nextValidTime = generateNextValidTime(jobInfo, fromTime);
        if (nextValidTime != null) {
            jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
            jobInfo.setTriggerNextTime(nextValidTime);
        } else {
            jobInfo.setTriggerStatus(0);
            jobInfo.setTriggerLastTime(null);
            jobInfo.setTriggerNextTime(null);
            LOGGER.warn(">>>>>>>>>>> xxl-job, refreshNextValidTime fail for job: jobId={}, scheduleType={}, scheduleConf={}",
                jobInfo.getId(), jobInfo.getScheduleType(), jobInfo.getScheduleConf());
        }
    }

    private void pushTimeRing(int ringSecond, Long jobId) {
        // push async ring
        List<Long> ringItemData = ringData.get(ringSecond);
        if (ringItemData == null) {
            ringItemData = new ArrayList<>();
            ringData.put(ringSecond, ringItemData);
        }
        ringItemData.add(jobId);

        LOGGER.debug(">>>>>>>>>>> xxl-job, schedule push time-ring : " + ringSecond + " = " + Arrays.asList(ringItemData));
    }

    public void stop() {

        // 1、stop schedule
        scheduleThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);  // wait
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        if (scheduleThread.getState() != Thread.State.TERMINATED) {
            // interrupt and wait
            scheduleThread.interrupt();
            try {
                scheduleThread.join();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        // if has ring data
        boolean hasRingData = false;
        if (!ringData.isEmpty()) {
            for (int second : ringData.keySet()) {
                List<Long> tmpData = ringData.get(second);
                if (tmpData != null && tmpData.size() > 0) {
                    hasRingData = true;
                    break;
                }
            }
        }
        if (hasRingData) {
            try {
                TimeUnit.SECONDS.sleep(8);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        // stop ring (wait job-in-memory stop)
        ringThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        if (ringThread.getState() != Thread.State.TERMINATED) {
            // interrupt and wait
            ringThread.interrupt();
            try {
                ringThread.join();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        LOGGER.info(">>>>>>>>>>> xxl-job, JobScheduleHelper stop");
    }


    // ---------------------- tools ----------------------
    public static LocalDateTime generateNextValidTime(JobInfo jobInfo, LocalDateTime fromTime) throws Exception {
        ScheduleType scheduleTypeEnum = jobInfo.getScheduleType();
        if (ScheduleType.CRON == scheduleTypeEnum) {
            return new CronExpression(jobInfo.getScheduleConf()).getNextValidTimeAfter(fromTime);
        } else if (ScheduleType.FIX_RATE == scheduleTypeEnum /*|| ScheduleTypeEnum.FIX_DELAY == scheduleTypeEnum*/) {
            return fromTime.plus(toInt(jobInfo.getScheduleConf()) * 1000, ChronoUnit.SECONDS);
        }
        return null;
    }


    private static Logger LOGGER = LoggerFactory.getLogger(JobScheduleHelper.class);

    private static volatile JobScheduleHelper instance ;

    private final JobScheduleContext jobScheduleContext;

    private JobScheduleHelper(JobScheduleContext jobScheduleContext) {
        this.jobScheduleContext = jobScheduleContext;
    }

}
