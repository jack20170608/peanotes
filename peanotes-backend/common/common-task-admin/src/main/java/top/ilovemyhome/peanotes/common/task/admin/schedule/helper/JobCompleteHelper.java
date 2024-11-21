package top.ilovemyhome.peanotes.common.task.admin.schedule.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.admin.core.model.HandleCallbackParam;
import top.ilovemyhome.peanotes.common.task.admin.core.model.ReturnT;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobLog;
import top.ilovemyhome.peanotes.common.task.admin.schedule.completer.JobCompleter;
import top.ilovemyhome.peanotes.common.task.admin.schedule.context.JobScheduleContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

/**
 * job lose-monitor instance
 *
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobCompleteHelper {


    public static JobCompleteHelper getInstance(JobScheduleContext jobScheduleContext) {
        if (INSTANCE == null) {
            synchronized (JobCompleteHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JobCompleteHelper(jobScheduleContext);
                }
            }
        }
        return INSTANCE;
    }

    // ---------------------- monitor ----------------------

    private ThreadPoolExecutor callbackThreadPool = null;
    private Thread monitorThread;
    private volatile boolean toStop = false;

    public void start() {

        // for callback
        callbackThreadPool = new ThreadPoolExecutor(
            2,
            20,
            30L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(3000),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "xxl-job, admin JobLosedMonitorHelper-callbackThreadPool-" + r.hashCode());
                }
            },
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    r.run();
                    logger.warn(">>>>>>>>>>> xxl-job, callback too fast, match threadpool rejected handler(run now).");
                }
            });


        // for monitor
        monitorThread = new Thread(() -> {

            // wait for JobTriggerPoolHelper-init
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                if (!toStop) {
                    logger.error(e.getMessage(), e);
                }
            }

            // monitor
            while (!toStop) {
                try {
                    // 任务结果丢失处理：调度记录停留在 "运行中" 状态超过10min，且对应执行器心跳注册失败不在线，则将本地调度主动标记失败；
                    LocalDateTime losedTime = LocalDateTime.now().minusMinutes(10);
                    List<Long> losedJobIds = jobScheduleContext.getJobLogDao().findLostJobIds(losedTime);
                    if (losedJobIds != null && losedJobIds.size() > 0) {
                        for (Long logId : losedJobIds) {
                            JobLog jobLog = new JobLog();
                            jobLog.setId(logId);

                            jobLog.setHandleTime(LocalDateTime.now());
                            jobLog.setHandleCode(ReturnT.FAIL_CODE);
                            jobLog.setHandleMsg("Job result lost, marked as failure");

                            JobCompleter jobCompleter = jobScheduleContext.getJobCompleter();
                            jobCompleter.updateHandleInfoAndFinish(jobLog);
                        }

                    }
                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(">>>>>>>>>>> xxl-job, job fail monitor thread error:{}", e);
                    }
                }

                try {
                    TimeUnit.SECONDS.sleep(60);
                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }
                }

            }

            logger.info(">>>>>>>>>>> xxl-job, JobLosedMonitorHelper stop");

        });
        monitorThread.setDaemon(true);
        monitorThread.setName("xxl-job, admin JobLosedMonitorHelper");
        monitorThread.start();
    }

    public void stop() {
        toStop = true;

        // stop registryOrRemoveThreadPool
        callbackThreadPool.shutdownNow();

        // stop monitorThread (interrupt and wait)
        monitorThread.interrupt();
        try {
            monitorThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }


    // ---------------------- helper ----------------------

    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {

        callbackThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                for (HandleCallbackParam handleCallbackParam : callbackParamList) {
                    ReturnT<String> callbackResult = callback(handleCallbackParam);
                    logger.debug(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                        (callbackResult.getCode() == ReturnT.SUCCESS_CODE ? "success" : "fail"), handleCallbackParam, callbackResult);
                }
            }
        });

        return ReturnT.SUCCESS;
    }

    private ReturnT<String> callback(HandleCallbackParam handleCallbackParam) {
        // valid log item
        JobLog log = jobScheduleContext.getJobLogDao().findOne(handleCallbackParam.getLogId()).orElse(null);
        if (log == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log item not found.");
        }
        if (log.getHandleCode() > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log repeate callback.");     // avoid repeat callback, trigger child job etc
        }

        // handle msg
        StringBuffer handleMsg = new StringBuffer();
        if (log.getHandleMsg() != null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (handleCallbackParam.getHandleMsg() != null) {
            handleMsg.append(handleCallbackParam.getHandleMsg());
        }

        // success, save log
        log.setHandleTime(LocalDateTime.now());
        log.setHandleCode(handleCallbackParam.getHandleCode());
        log.setHandleMsg(handleMsg.toString());
        jobCompleter.updateHandleInfoAndFinish(log);

        return ReturnT.SUCCESS;
    }

    private static Logger logger = LoggerFactory.getLogger(JobCompleteHelper.class);

    private volatile static JobCompleteHelper INSTANCE;

    private JobCompleteHelper(JobScheduleContext jobScheduleContext) {
        this.jobScheduleContext = jobScheduleContext;
        this.jobCompleter = jobScheduleContext.getJobCompleter();
    }

    private final JobCompleter jobCompleter;

    private final JobScheduleContext jobScheduleContext;


}
