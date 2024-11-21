package top.ilovemyhome.peanotes.common.task.admin.schedule.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.TriggerTypeEnum;
import top.ilovemyhome.peanotes.common.task.admin.schedule.context.JobScheduleContext;
import top.ilovemyhome.peanotes.common.task.admin.schedule.trigger.JobTrigger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * job trigger thread pool helper
 *
 * @author xuxueli 2018-07-03 21:08:07
 */
public class JobTriggerPoolHelper {


    public void start() {
        fastTriggerPool = new ThreadPoolExecutor(
            10,
            jobScheduleContext.getTriggerPoolFastMax(),
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1000),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "xxl-job, admin JobTriggerPoolHelper-fastTriggerPool-" + r.hashCode());
                }
            });

        slowTriggerPool = new ThreadPoolExecutor(
            10,
            jobScheduleContext.getTriggerPoolSlowMax(),
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(2000),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "xxl-job, admin JobTriggerPoolHelper-slowTriggerPool-" + r.hashCode());
                }
            });
    }


    public void stop() {
        //triggerPool.shutdown();
        fastTriggerPool.shutdownNow();
        slowTriggerPool.shutdownNow();
        LOGGER.info(">>>>>>>>> xxl-job trigger thread pool shutdown success.");
    }


    /**
     * add trigger
     */
    public void addTrigger(final Long jobId,
                           final TriggerTypeEnum triggerType,
                           final int failRetryCount,
                           final String executorShardingParam,
                           final String executorParam,
                           final String addressList) {

        // choose thread pool
        ThreadPoolExecutor triggerPool_ = fastTriggerPool;
        AtomicInteger jobTimeoutCount = jobTimeoutCountMap.get(jobId);
        if (jobTimeoutCount != null && jobTimeoutCount.get() > 10) {      // job-timeout 10 times in 1 min
            triggerPool_ = slowTriggerPool;
        }

        // trigger
        triggerPool_.execute(() -> {
            long start = System.currentTimeMillis();

            try {
                // do trigger
                jobTrigger.trigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            } finally {

                // check timeout-count-map
                long minTim_now = System.currentTimeMillis() / 60000;
                if (minTim != minTim_now) {
                    minTim = minTim_now;
                    jobTimeoutCountMap.clear();
                }

                // incr timeout-count-map
                long cost = System.currentTimeMillis() - start;
                if (cost > 500) {       // ob-timeout threshold 500ms
                    AtomicInteger timeoutCount = jobTimeoutCountMap.putIfAbsent(jobId, new AtomicInteger(1));
                    if (timeoutCount != null) {
                        timeoutCount.incrementAndGet();
                    }
                }

            }

        });
    }





    /**
     * @param jobId
     * @param triggerType
     * @param failRetryCount        >=0: use this param
     *                              <0: use param from job info config
     * @param executorShardingParam
     * @param executorParam         null: use job param
     *                              not null: cover job param
     */
    public static void trigger(Long jobId, TriggerTypeEnum triggerType, int failRetryCount, String executorShardingParam, String executorParam, String addressList) {
        INSTANCE.addTrigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
    }



    // ---------------------- helper ----------------------
    public static JobTriggerPoolHelper getInstance(JobScheduleContext context) {
        if (INSTANCE == null) {
            synchronized (JobTriggerPoolHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JobTriggerPoolHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    private JobTriggerPoolHelper(JobScheduleContext jobScheduleContext) {
        this.jobScheduleContext = jobScheduleContext;
        this.jobTrigger = new JobTrigger(jobScheduleContext);
    }
    private final static Logger LOGGER = LoggerFactory.getLogger(JobTriggerPoolHelper.class);

    // fast/slow thread pool
    private ThreadPoolExecutor fastTriggerPool = null;
    private ThreadPoolExecutor slowTriggerPool = null;
    private final JobTrigger jobTrigger ;

    // job timeout count
    private volatile long minTim = System.currentTimeMillis() / 60000;     // ms > min
    private final ConcurrentMap<Long, AtomicInteger> jobTimeoutCountMap = new ConcurrentHashMap<>();


    private volatile static JobTriggerPoolHelper INSTANCE;
    private final JobScheduleContext jobScheduleContext;
}
