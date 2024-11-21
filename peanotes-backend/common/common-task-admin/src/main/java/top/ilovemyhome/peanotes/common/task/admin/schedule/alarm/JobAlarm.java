package top.ilovemyhome.peanotes.common.task.admin.schedule.alarm;

import top.ilovemyhome.peanotes.common.task.admin.domain.JobInfo;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobLog;

/**
 * @author xuxueli 2020-01-19
 */
public interface JobAlarm {

    boolean doAlarm(JobInfo info, JobLog jobLog);

    boolean alarm(JobInfo info, JobLog jobLog);

}
