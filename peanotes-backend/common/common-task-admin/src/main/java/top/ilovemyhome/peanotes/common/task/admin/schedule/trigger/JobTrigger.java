package top.ilovemyhome.peanotes.common.task.admin.schedule.trigger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.admin.core.biz.ExecutorBiz;
import top.ilovemyhome.peanotes.common.task.admin.core.exception.ThrowableUtil;
import top.ilovemyhome.peanotes.common.task.admin.core.model.ReturnT;
import top.ilovemyhome.peanotes.common.task.admin.core.model.TriggerParam;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.ExecutorBlockStrategyEnum;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.TriggerTypeEnum;
import top.ilovemyhome.peanotes.common.task.admin.core.route.ExecutorRouteStrategyEnum;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobGroupDao;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobInfoDao;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobLogDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobGroup;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobInfo;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobLog;
import top.ilovemyhome.peanotes.common.task.admin.schedule.JobScheduler;
import top.ilovemyhome.peanotes.common.task.admin.schedule.context.JobScheduleContext;
import top.ilovemyhome.peanotes.common.task.admin.core.common.IpUtil;

import java.time.LocalDateTime;

/**
 * xxl-job trigger
 * Created by xuxueli on 17/7/13.
 */
public class JobTrigger {
    public JobTrigger(JobScheduleContext jobScheduleContext) {
        this.jobScheduleContext = jobScheduleContext;
    }

    /**
     * trigger job
     *
     * @param jobId
     * @param triggerType
     * @param failRetryCount        >=0: use this param
     *                              <0: use param from job info config
     * @param executorShardingParam
     * @param executorParam         null: use job param
     *                              not null: cover job param
     * @param addressList           null: use executor addressList
     *                              not null: cover
     */
    public void trigger(
        Long jobId
        , TriggerTypeEnum triggerType
        , int failRetryCount
        , String executorShardingParam
        , String executorParam
        , String addressList) {
        JobInfoDao jobInfoDao = jobScheduleContext.getJobInfoDao();
        JobGroupDao jobGroupDao = jobScheduleContext.getJobGroupDao();
        // load data
        JobInfo jobInfo = jobInfoDao.findOne(jobId).orElse(null);
        if (jobInfo == null) {
            LOGGER.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }
        if (executorParam != null) {
            jobInfo.setExecutorParam(executorParam);
        }
        int finalFailRetryCount = failRetryCount >= 0 ? failRetryCount : jobInfo.getExecutorFailRetryCount();
        JobGroup group = jobGroupDao.findOne(jobInfo.getJobGroupId()).orElse(null);

        // cover addressList
        if (addressList != null && addressList.trim().length() > 0) {
            group.setAddressType(1);
            group.setAddressList(addressList.trim());
        }

        // sharding param
        int[] shardingParam = null;
        if (executorShardingParam != null) {
            String[] shardingArr = executorShardingParam.split("/");
            if (shardingArr.length == 2 && isNumeric(shardingArr[0]) && isNumeric(shardingArr[1])) {
                shardingParam = new int[2];
                shardingParam[0] = Integer.valueOf(shardingArr[0]);
                shardingParam[1] = Integer.valueOf(shardingArr[1]);
            }
        }
        if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == jobInfo.getExecutorRouteStrategy()
            && group.getRegistryList() != null && !group.getRegistryList().isEmpty()
            && shardingParam == null) {
            for (int i = 0; i < group.getRegistryList().size(); i++) {
                processTrigger(group, jobInfo, finalFailRetryCount, triggerType, i, group.getRegistryList().size());
            }
        } else {
            if (shardingParam == null) {
                shardingParam = new int[]{0, 1};
            }
            processTrigger(group, jobInfo, finalFailRetryCount, triggerType, shardingParam[0], shardingParam[1]);
        }

    }

    private static boolean isNumeric(String str) {
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param group               job group, registry list may be empty
     * @param jobInfo
     * @param finalFailRetryCount
     * @param triggerType
     * @param index               sharding index
     * @param total               sharding index
     */
    private void processTrigger(JobGroup group, JobInfo jobInfo, int finalFailRetryCount, TriggerTypeEnum triggerType, int index, int total) {

        // param
        ExecutorBlockStrategyEnum blockStrategy = jobInfo.getExecutorBlockStrategy();  // block strategy
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = jobInfo.getExecutorRouteStrategy();    // route strategy
        String shardingParam = (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum)
            ? String.valueOf(index).concat("/").concat(String.valueOf(total)) : null;

        // 1、save log-id
        JobLog jobLog = new JobLog();
        jobLog.setJobGroupId(jobInfo.getJobGroupId());
        jobLog.setJobId(jobInfo.getId());
        jobLog.setTriggerTime(LocalDateTime.now());

        JobLogDao jobLogDao = jobScheduleContext.getJobLogDao();
        jobLogDao.create(jobLog);
        LOGGER.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

        // 2、init trigger-param
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
        triggerParam.setExecutorParams(jobInfo.getExecutorParam());
        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        triggerParam.setExecutorTimeout(jobInfo.getExecutorTimeout());
        triggerParam.setLogId(jobLog.getId());
        triggerParam.setLogDateTime(jobLog.getTriggerTime());
        triggerParam.setGlueType(jobInfo.getGlueType());
        triggerParam.setGlueSource(jobInfo.getGlueSource());
        triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdateTime());
        triggerParam.setBroadcastIndex(index);
        triggerParam.setBroadcastTotal(total);

        // 3、init address
        String address = null;
        ReturnT<String> routeAddressResult = null;
        if (group.getRegistryList() != null && !group.getRegistryList().isEmpty()) {
            if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum) {
                if (index < group.getRegistryList().size()) {
                    address = group.getRegistryList().get(index);
                } else {
                    address = group.getRegistryList().get(0);
                }
            } else {
                routeAddressResult = executorRouteStrategyEnum.getRouter().route(triggerParam, group.getRegistryList());
                if (routeAddressResult.getCode() == ReturnT.SUCCESS_CODE) {
                    address = routeAddressResult.getContent();
                }
            }
        } else {
            routeAddressResult = new ReturnT<>(ReturnT.FAIL_CODE, "Registry List is empty");
        }

        // 4、trigger remote executor
        ReturnT<String> triggerResult = null;
        if (address != null) {
            triggerResult = runExecutor(triggerParam, address);
        } else {
            triggerResult = new ReturnT<String>(ReturnT.FAIL_CODE, null);
        }

        // 5、collection trigger info
        StringBuffer triggerMsgSb = new StringBuffer();
        triggerMsgSb.append("Trigger Type: ").append(triggerType.getTitle());
        triggerMsgSb.append("<br>").append("Trigger Admin Ip: ").append(IpUtil.getIp());
        triggerMsgSb.append("<br>").append("Execotor-Registry Type:")
            .append((group.getAddressType() == 0) ? "Automatic registration" : "Manual registration");
        triggerMsgSb.append("<br>").append("Execotor-Registry Address: ").append(group.getRegistryList());
        triggerMsgSb.append("<br>").append("Route Strategy:").append(executorRouteStrategyEnum.getTitle());
        if (shardingParam != null) {
            triggerMsgSb.append("(" + shardingParam + ")");
        }
        triggerMsgSb.append("<br>").append("Block Strategy: ").append(blockStrategy.getTitle());
        triggerMsgSb.append("<br>").append("Job timeout period: ").append(jobInfo.getExecutorTimeout());
        triggerMsgSb.append("<br>").append("Fail Retry Count:").append(finalFailRetryCount);

        triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>Trigger Job <<<<<<<<<<< </span><br>")
            .append((routeAddressResult != null && routeAddressResult.getMsg() != null) ? routeAddressResult.getMsg() + "<br><br>" : "").append(triggerResult.getMsg() != null ? triggerResult.getMsg() : "");

        // 6、save log trigger-info
        jobLog.setExecutorAddress(address);
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setExecutorShardingParam(shardingParam);
        jobLog.setExecutorFailRetryCount(finalFailRetryCount);
        //jobLog.setTriggerTime();
        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(triggerMsgSb.toString());
        jobLogDao.update(jobLog.getId(), jobLog);

        LOGGER.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
    }

    /**
     * run executor
     *
     * @param triggerParam
     * @param address
     * @return
     */
    private ReturnT<String> runExecutor(TriggerParam triggerParam, String address) {
        ReturnT<String> runResult = null;
        JobScheduler jobScheduler = this.jobScheduleContext.getJobScheduler();
        try {
            ExecutorBiz executorBiz = jobScheduler.getExecutorBiz(address);
            runResult = executorBiz.run(triggerParam);
        } catch (Exception e) {
            LOGGER.error(">>>>>>>>>>> xxl-job trigger error, please check if the executor[{}] is running.", address, e);
            runResult = new ReturnT<String>(ReturnT.FAIL_CODE, ThrowableUtil.toString(e));
        }

        StringBuffer runResultSB = new StringBuffer("Trigger Job:");
        runResultSB.append("<br>address：").append(address);
        runResultSB.append("<br>code：").append(runResult.getCode());
        runResultSB.append("<br>msg：").append(runResult.getMsg());

        runResult.setMsg(runResultSB.toString());
        return runResult;
    }

    private final static Logger LOGGER = LoggerFactory.getLogger(JobTrigger.class);

    private final JobScheduleContext jobScheduleContext;


}
