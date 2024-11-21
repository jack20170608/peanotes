package top.ilovemyhome.peanotes.common.task.admin.core.biz.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.admin.core.biz.ExecutorBiz;
import top.ilovemyhome.peanotes.common.task.admin.core.executor.JobExecutor;
import top.ilovemyhome.peanotes.common.task.admin.core.glue.GlueTypeEnum;
import top.ilovemyhome.peanotes.common.task.admin.core.handler.IJobHandler;
import top.ilovemyhome.peanotes.common.task.admin.core.handler.impl.ScriptJobHandler;
import top.ilovemyhome.peanotes.common.task.admin.core.log.JobFileAppender;
import top.ilovemyhome.peanotes.common.task.admin.core.model.*;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.ExecutorBlockStrategyEnum;
import top.ilovemyhome.peanotes.common.task.admin.core.thread.JobThread;

/**
 * Created by xuxueli on 17/3/1.
 */
public class ExecutorBizImpl implements ExecutorBiz {
    private static Logger logger = LoggerFactory.getLogger(ExecutorBizImpl.class);

    @Override
    public ReturnT<String> beat() {
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> idleBeat(IdleBeatParam idleBeatParam) {

        // isRunningOrHasQueue
        boolean isRunningOrHasQueue = false;
        JobThread jobThread = JobExecutor.loadJobThread(idleBeatParam.getJobId());
        if (jobThread != null && jobThread.isRunningOrHasQueue()) {
            isRunningOrHasQueue = true;
        }

        if (isRunningOrHasQueue) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "job thread is running or has trigger queue.");
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> run(TriggerParam triggerParam) {
        // load old：jobHandler + jobThread
        JobThread jobThread = JobExecutor.loadJobThread(triggerParam.getJobId());
        IJobHandler jobHandler = jobThread != null ? jobThread.getHandler() : null;
        String removeOldReason = null;

        // valid：jobHandler + jobThread
        GlueTypeEnum glueTypeEnum = triggerParam.getGlueType();
        if (GlueTypeEnum.BEAN == glueTypeEnum) {

            // new jobhandler
            IJobHandler newJobHandler = JobExecutor.loadJobHandler(triggerParam.getExecutorHandler());

            // valid old jobThread
            if (jobThread != null && jobHandler != newJobHandler) {
                // change handler, need kill old thread
                removeOldReason = "change jobhandler or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (jobHandler == null) {
                jobHandler = newJobHandler;
                if (jobHandler == null) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, "job handler [" + triggerParam.getExecutorHandler() + "] not found.");
                }
            }

        }  else if (glueTypeEnum != null && glueTypeEnum.isScript()) {

            // valid old jobThread
            if (jobThread != null &&
                !(jobThread.getHandler() instanceof ScriptJobHandler
                    && ((ScriptJobHandler) jobThread.getHandler()).getGlueUpdatetime().equals(triggerParam.getGlueUpdatetime()))) {
                // change script or gluesource updated, need kill old thread
                removeOldReason = "change job source or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (jobHandler == null) {
                jobHandler = new ScriptJobHandler(triggerParam.getJobId(), triggerParam.getGlueUpdatetime()
                    , triggerParam.getGlueSource(), triggerParam.getGlueType());
            }
        } else {
            return new ReturnT<>(ReturnT.FAIL_CODE, "glueType[" + triggerParam.getGlueType() + "] is not valid.");
        }

        // executor block strategy
        if (jobThread != null) {
            ExecutorBlockStrategyEnum blockStrategy = triggerParam.getExecutorBlockStrategy();
            if (ExecutorBlockStrategyEnum.DISCARD_LATER == blockStrategy) {
                // discard when running
                if (jobThread.isRunningOrHasQueue()) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, "block strategy effect：" + ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle());
                }
            } else if (ExecutorBlockStrategyEnum.COVER_EARLY == blockStrategy) {
                // kill running jobThread
                if (jobThread.isRunningOrHasQueue()) {
                    removeOldReason = "block strategy effect：" + ExecutorBlockStrategyEnum.COVER_EARLY.getTitle();

                    jobThread = null;
                }
            } else {
                // just queue trigger
            }
        }

        // replace thread (new or exists invalid)
        if (jobThread == null) {
            jobThread = JobExecutor.registJobThread(triggerParam.getJobId(), jobHandler, removeOldReason);
        }

        // push data to queue
        ReturnT<String> pushResult = jobThread.pushTriggerQueue(triggerParam);
        return pushResult;
    }

    @Override
    public ReturnT<String> kill(KillParam killParam) {
        // kill handlerThread, and create new one
        JobThread jobThread = JobExecutor.loadJobThread(killParam.getJobId());
        if (jobThread != null) {
            JobExecutor.removeJobThread(killParam.getJobId(), "scheduling center kill job.");
            return ReturnT.SUCCESS;
        }

        return new ReturnT<String>(ReturnT.SUCCESS_CODE, "job thread already killed.");
    }

    @Override
    public ReturnT<LogResult> log(LogParam logParam) {
        // log filename: logPath/yyyy-MM-dd/9999.log
        String logFileName = JobFileAppender.makeLogFileName(logParam.getLogDateTim(), logParam.getLogId());

        LogResult logResult = JobFileAppender.readLog(logFileName, logParam.getFromLineNum());
        return new ReturnT<LogResult>(logResult);
    }

}
