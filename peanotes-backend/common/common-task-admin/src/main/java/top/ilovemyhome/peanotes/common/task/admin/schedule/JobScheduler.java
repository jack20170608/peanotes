package top.ilovemyhome.peanotes.common.task.admin.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.admin.core.biz.ExecutorBiz;
import top.ilovemyhome.peanotes.common.task.admin.core.biz.client.ExecutorBizClient;
import top.ilovemyhome.peanotes.common.task.admin.schedule.context.JobScheduleContext;
import top.ilovemyhome.peanotes.common.task.admin.schedule.helper.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xuxueli 2018-10-28 00:18:17
 */

public class JobScheduler {

    private final JobScheduleContext context;

    private final JobTriggerPoolHelper jobTriggerPoolHelper;
    private final JobRegistryHelper jobRegistryHelper;
    private final JobFailMonitorHelper jobFailMonitorHelper;
    private final JobCompleteHelper jobCompleteHelper;
    private final JobLogReportHelper jobLogReportHelper;
    private final JobScheduleHelper jobScheduleHelper;

    public JobScheduler(JobScheduleContext jobScheduleContext) {
        this.context = jobScheduleContext;
        jobTriggerPoolHelper = JobTriggerPoolHelper.getInstance(jobScheduleContext);
        jobRegistryHelper = JobRegistryHelper.getInstance(jobScheduleContext);
        jobFailMonitorHelper = JobFailMonitorHelper.getInstance(jobScheduleContext);
        jobCompleteHelper = JobCompleteHelper.getInstance(jobScheduleContext);
        jobLogReportHelper = JobLogReportHelper.getInstance(jobScheduleContext);
        jobScheduleHelper = JobScheduleHelper.getInstance(jobScheduleContext);

    }

    public void init() throws Exception {
        // admin trigger pool start
        jobTriggerPoolHelper.start();

        // admin registry monitor run
        jobRegistryHelper.start();

        // admin fail-monitor run
        jobFailMonitorHelper.start();

        // admin lose-monitor run ( depend on JobTriggerPoolHelper )
        jobCompleteHelper.start();

        // admin log report start
        jobLogReportHelper.start();

        // start-schedule  ( depend on JobTriggerPoolHelper )
        jobScheduleHelper.start();

        LOGGER.info(">>>>>>>>> init xxl-job admin success.");
    }


    public void destroy() throws Exception {

        // stop-schedule
        jobScheduleHelper.stop();

        // admin log report stop
        jobLogReportHelper.stop();

        // admin lose-monitor stop
        jobCompleteHelper.stop();

        // admin fail-monitor stop
        jobFailMonitorHelper.stop();

        // admin registry stop
        jobRegistryHelper.stop();

        // admin trigger pool stop
        jobTriggerPoolHelper.stop();

    }


    // ---------------------- executor-client ----------------------
    private ConcurrentMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<String, ExecutorBiz>();

    public ExecutorBiz getExecutorBiz(String address) {
        // valid
        if (address==null || address.trim().length()==0) {
            return null;
        }

        // load-cache
        address = address.trim();
        ExecutorBiz executorBiz = executorBizRepository.get(address);
        if (executorBiz != null) {
            return executorBiz;
        }

        // set-cache
        executorBiz = new ExecutorBizClient(address, this.context.getAccessToken());

        executorBizRepository.put(address, executorBiz);
        return executorBiz;
    }

    private volatile static JobScheduler INSTANCE = null;

    public static JobScheduler getInstance() {
        return INSTANCE;
    }

    public static JobScheduler getInstance(JobScheduleContext jobScheduleContext) {
        if (INSTANCE == null && jobScheduleContext != null) {
            synchronized (JobScheduler.class) {
                if (INSTANCE == null) {
                    return new JobScheduler(jobScheduleContext);
                }
            }
        }
        return INSTANCE;
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);


}
