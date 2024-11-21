package top.ilovemyhome.peanotes.common.task.admin.schedule.completer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.admin.core.job.JobContext;
import top.ilovemyhome.peanotes.common.task.admin.core.model.ReturnT;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.TriggerTypeEnum;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobInfoDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobInfo;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobLog;
import top.ilovemyhome.peanotes.common.task.admin.schedule.helper.JobTriggerPoolHelper;
import top.ilovemyhome.peanotes.common.task.admin.schedule.context.JobScheduleContext;

import java.text.MessageFormat;

/**
 * @author xuxueli 2020-10-30 20:43:10
 */
public class JobCompleter {

    private static Logger logger = LoggerFactory.getLogger(JobCompleter.class);
    private JobScheduleContext jobScheduleContext;

    public JobCompleter(JobScheduleContext jobScheduleContext) {
        this.jobScheduleContext = jobScheduleContext;
    }

    /**
     * common fresh handle entrance (limit only once)
     *
     * @param xxlJobLog
     * @return
     */
    public int updateHandleInfoAndFinish(JobLog xxlJobLog) {

        // finish
        finishJob(xxlJobLog);

        // text最大64kb 避免长度过长
        if (xxlJobLog.getHandleMsg().length() > 15000) {
            xxlJobLog.setHandleMsg(xxlJobLog.getHandleMsg().substring(0, 15000));
        }

        // fresh handle
        return jobScheduleContext.getJobLogDao().updateHandleInfo(xxlJobLog);
    }


    /**
     * do somethind to finish job
     */
    private void finishJob(JobLog xxlJobLog) {

        // 1、handle success, to trigger child job
        String triggerChildMsg = null;
        JobInfoDao jobInfoDao = jobScheduleContext.getJobInfoDao();
        if (JobContext.HANDLE_CODE_SUCCESS == xxlJobLog.getHandleCode()) {
            JobInfo xxlJobInfo = jobInfoDao.findOne(xxlJobLog.getJobId()).orElse(null);
            if (xxlJobInfo != null && xxlJobInfo.getChildJobId() != null && xxlJobInfo.getChildJobId().trim().length() > 0) {
                triggerChildMsg = "<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>Trigger child job<<<<<<<<<<< </span><br>";

                String[] childJobIds = xxlJobInfo.getChildJobId().split(",");
                for (int i = 0; i < childJobIds.length; i++) {
                    Long childJobId = (childJobIds[i] != null && childJobIds[i].trim().length() > 0 && isNumeric(childJobIds[i])) ? Long.valueOf(childJobIds[i]) : -1;
                    if (childJobId > 0) {

                        JobTriggerPoolHelper.trigger(childJobId, TriggerTypeEnum.PARENT, -1, null, null, null);
                        ReturnT<String> triggerChildResult = ReturnT.SUCCESS;

                        // add msg
                        triggerChildMsg += MessageFormat.format("{0}/{1} [Job ID={2}], Trigger {3}, Trigger msg: {4} <br>",
                            (i + 1),
                            childJobIds.length,
                            childJobIds[i],
                            (triggerChildResult.getCode() == ReturnT.SUCCESS_CODE ? "system_success" : "system_fail"),
                            triggerChildResult.getMsg());
                    } else {
                        triggerChildMsg += MessageFormat.format("{0}/{1} [Job ID={2}], Trigger Fail, Trigger msg: Job ID is illegal <br>",
                            (i + 1),
                            childJobIds.length,
                            childJobIds[i]);
                    }
                }

            }
        }

        if (triggerChildMsg != null) {
            xxlJobLog.setHandleMsg(xxlJobLog.getHandleMsg() + triggerChildMsg);
        }

        // 2、fix_delay trigger next
        // on the way

    }

    private static boolean isNumeric(String str) {
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
