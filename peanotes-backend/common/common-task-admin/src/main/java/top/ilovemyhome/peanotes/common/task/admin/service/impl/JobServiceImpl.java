package top.ilovemyhome.peanotes.common.task.admin.service.impl;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Page;
import top.ilovemyhome.peanotes.backend.common.utils.StringConvertUtils;
import top.ilovemyhome.peanotes.common.task.admin.application.AppContext;
import top.ilovemyhome.peanotes.common.task.admin.core.cron.CronExpression;
import top.ilovemyhome.peanotes.common.task.admin.core.glue.GlueTypeEnum;
import top.ilovemyhome.peanotes.common.task.admin.schedule.helper.JobScheduleHelper;
import top.ilovemyhome.peanotes.common.task.admin.schedule.helper.JobTriggerPoolHelper;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.TriggerTypeEnum;
import top.ilovemyhome.peanotes.common.task.admin.core.model.enums.ScheduleType;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobGroupDao;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobInfoDao;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobLogDao;
import top.ilovemyhome.peanotes.common.task.admin.dao.JobLogGlueDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobGroup;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobInfo;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobUser;
import top.ilovemyhome.peanotes.common.task.admin.service.JobService;
import top.ilovemyhome.peanotes.common.task.admin.web.dto.JobInfoQueryDto;
import top.ilovemyhome.peanotes.common.task.admin.web.handlers.helper.R;
import top.ilovemyhome.peanotes.common.task.admin.web.handlers.helper.RCode;

import java.time.LocalDateTime;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNumeric;

public class JobServiceImpl implements JobService {

    public JobServiceImpl(AppContext appContext) {
        this.jobInfoDao = appContext.getBean("jobInfoDao", JobInfoDao.class);
        this.jobGroupDao = appContext.getBean("jobGroupDao", JobGroupDao.class);
        this.jobLogInfoDao = appContext.getBean("jobLogInfoDao", JobLogDao.class);
        this.jobLogGlueDao = appContext.getBean("jobLogGlueDao", JobLogGlueDao.class);
        this.jdbi = appContext.getDataSourceFactory().getJdbi();
    }


    @Override
    public Page<JobInfo> query(JobInfoQueryDto queryDto) {
        return this.jobInfoDao.query(queryDto);
    }

    @Override
    public R<Long> add(JobInfo jobInfo) {
        R<Long> r = validation(jobInfo);
        if (!r.isSuccess()) {
            return r;
        }
        //Persist the job info
        LocalDateTime now = LocalDateTime.now();
        jobInfo.setAddTime(now);
        jobInfo.setUpdateTime(now);
        jobInfo.setGlueUpdateTime(now);
        Long id = jobInfoDao.create(jobInfo);
        LOGGER.info("Job persist successfully with id=[{}].", id);
        if (Objects.isNull(id)) {
            return R.fail(RCode.INTERNAL_SQL_ERROR.getCode(), "Job info persist failure.");
        }
        return R.ok(id);
    }

    @Override
    public R<Long> update(JobInfo jobInfo) {
        //Validate the existing job
        if (Objects.isNull(jobInfo.getId())) {
            return R.fail(RCode.NOT_FOUND.getCode(), "Empty job id.");
        }
        JobInfo existingJobInfo = jobInfoDao.findOne(jobInfo.getId()).orElse(null);
        if (Objects.isNull(existingJobInfo)) {
            return R.fail(RCode.NOT_FOUND.getCode(), "Not exist job with id " + jobInfo.getId() + " .");
        }
        //Validate info
        R r = validation(jobInfo);
        if (!r.isSuccess()) {
            return r;
        }
        jobInfo.setUpdateTime(LocalDateTime.now());
        // next trigger time (5s后生效，避开预读周期)
        LocalDateTime nextTriggerTime = null;
        boolean scheduleDataNotChanged = jobInfo.getScheduleType().equals(existingJobInfo.getScheduleType())
            && jobInfo.getScheduleConf().equals(existingJobInfo.getScheduleConf());
        if (existingJobInfo.getTriggerStatus() == 1 && !scheduleDataNotChanged) {
            try {
                nextTriggerTime = JobScheduleHelper.generateNextValidTime(jobInfo, LocalDateTime.now().plusSeconds(5));
                if (nextTriggerTime == null) {
                    return R.fail(RCode.BAD_REQUEST.getCode(), "Not a valid schedule time.");
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                return R.fail(RCode.BAD_REQUEST.getCode(), "Get next trigger time failure.");
            }
        }
        jobInfo.setTriggerNextTime(nextTriggerTime);
        int count = jobInfoDao.update(jobInfo.getId(), jobInfo);
        if (count < 1) {
            return R.fail(RCode.INTERNAL_SQL_ERROR.getCode(), "Job info update failure.");
        }
        return R.ok(jobInfo.getId());
    }

    @Override
    public R<Long> remove(Long id) {
        //不存在的话，直接返回成功，幂等性操作
        JobInfo jobInfo = jobInfoDao.findOne(id).orElse(null);
        if (jobInfo == null) {
            return R.ok(id);
        }
        jdbi.useTransaction(handle -> {
            jobInfoDao.delete(id);
            jobLogInfoDao.deleteByJobId(id);
            jobLogGlueDao.deleteByJobId(id);
        });
        return R.ok(id, "Delete success");
    }

    @Override
    public R<Long> start(Long id) {
        JobInfo jobInfo = jobInfoDao.findOne(id).orElse(null);
        if (Objects.isNull(jobInfo)) {
            return R.fail(RCode.NOT_FOUND.getCode(), "Empty job for id=[" + id + "].");
        }

        // valid
        if (ScheduleType.NONE == jobInfo.getScheduleType()) {
            return R.fail(RCode.BAD_REQUEST.getCode(), "Job schedule type is none.");
        }

        // next trigger time (5s后生效，避开预读周期)
        LocalDateTime nextTriggerTime = getNextTriggerTime(jobInfo);
        jobInfo.setTriggerStatus(1);
        jobInfo.setTriggerLastTime(null);
        jobInfo.setTriggerNextTime(nextTriggerTime);
        jobInfo.setUpdateTime(LocalDateTime.now());
        jobInfoDao.update(id, jobInfo);
        return R.ok(id, "Start success");
    }

    @Override
    public R<Long> stop(Long id) {
        JobInfo jobInfo = jobInfoDao.findOne(id).orElse(null);
        if (Objects.isNull(jobInfo)) {
            return R.fail(RCode.NOT_FOUND.getCode(), "No job for id=[" + id + "].");
        }
        jobInfo.setTriggerStatus(0);
        jobInfo.setTriggerLastTime(null);
        jobInfo.setTriggerNextTime(null);
        jobInfo.setUpdateTime(LocalDateTime.now());
        jobInfoDao.update(id, jobInfo);
        return R.ok(id);
    }

    @Override
    public R<Long> trigger(JobUser loginUser, Long jobId, String executorParam, String addressList) {
        // permission
        if (loginUser == null) {
            return R.fail(RCode.BAD_REQUEST.getCode(), "No login user.");
        }
        JobInfo jobInfo = jobInfoDao.findOne(jobId).orElse(null);
        if (jobInfo == null) {
            return R.fail(RCode.NOT_FOUND.getCode(), "No job for id=[" + jobId + "].");
        }
        if (!hasPermission(loginUser, jobInfo.getJobGroupId())) {
            return R.fail(RCode.NO_PERMISSION.getCode(), "No permission to trigger job with id=[" + jobId + "].");
        }
        // force cover job param
        if (executorParam == null) {
            executorParam = "";
        }
        JobTriggerPoolHelper.trigger(jobId, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
        return R.ok(jobId);
    }

    private boolean hasPermission(JobUser loginUser, Long jobGroupId){
        if (loginUser.getRole() == 1) {
            return true;
        }
        List<String> groupIdStrs = new ArrayList<>();
        if (loginUser.getPermission()!=null && loginUser.getPermission().trim().length()>0) {
            groupIdStrs = Arrays.asList(loginUser.getPermission().trim().split(","));
        }
        return groupIdStrs.contains(String.valueOf(jobGroupId));
    }

    private LocalDateTime getNextTriggerTime(JobInfo jobInfo) {
        LocalDateTime nextTriggerTime;
        try {
            nextTriggerTime = JobScheduleHelper.generateNextValidTime(jobInfo, LocalDateTime.now().plusSeconds(5));
            if (nextTriggerTime == null) {
                throw new ClientErrorException("Empty trigger time.", Response.Status.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ClientErrorException("Get next trigger time failure.", Response.Status.INTERNAL_SERVER_ERROR);
        }
        return nextTriggerTime;
    }

    private R<Long> validation(JobInfo jobInfo) {
        Optional<JobGroup> jobGroupOptional = jobGroupDao.findOne(jobInfo.getJobGroupId());
        if (jobGroupOptional.isEmpty()) {
            return R.fail(RCode.BAD_REQUEST.getCode(), "Not found the job group with id " + jobInfo.getJobGroupId());
        }
        if (StringUtils.isBlank(jobInfo.getJobDesc())) {
            return R.fail(RCode.BAD_REQUEST.getCode(), "Blank job description.");
        }
        if (StringUtils.isBlank(jobInfo.getAuthor())) {
            return R.fail(RCode.BAD_REQUEST.getCode(), "Blank author.");
        }
        //The schedule type should not null
        if (Objects.isNull(jobInfo.getScheduleType())) {
            return R.fail(RCode.BAD_REQUEST.getCode(), "Schedule type is null.");
        }
        switch (jobInfo.getScheduleType()) {
            case CRON -> {
                if (jobInfo.getScheduleConf() == null || !CronExpression.isValidExpression(jobInfo.getScheduleConf())) {
                    return R.fail(RCode.BAD_REQUEST.getCode(), "Invalid CRON expression " + jobInfo.getScheduleConf());
                }
            }
            case FIX_RATE -> {
                if (StringUtils.isBlank(jobInfo.getScheduleConf())) {
                    return R.fail(RCode.BAD_REQUEST.getCode(), "Schedule conf is blank.");
                }
                Integer fixSecond = StringConvertUtils.toInt(jobInfo.getScheduleConf());
                if (Objects.isNull(fixSecond)) {
                    return R.fail(RCode.BAD_REQUEST.getCode(), "Schedule conf cannot parse to a number.");
                }
                if (fixSecond < 1) {
                    return R.fail(RCode.BAD_REQUEST.getCode(), "Schedule conf for fixed rate should not less than 1.");
                }
            }
            default -> {
                return R.fail(RCode.BAD_REQUEST.getCode(), "Unsupported schedule type + " + jobInfo.getScheduleType());
            }
        }
        if (jobInfo.getGlueType() == null) {
            return R.fail(RCode.BAD_REQUEST.getCode(), "Glue type is null.");
        }
        if (Objects.requireNonNull(jobInfo.getGlueType()) == GlueTypeEnum.BEAN) {
            if (StringUtils.isBlank(jobInfo.getExecutorHandler())) {
                return R.fail(RCode.BAD_REQUEST.getCode(), "Bean executor handler is null.");
            }
        } else {
            if (StringUtils.isBlank(jobInfo.getGlueSource())) {
                return R.fail(RCode.BAD_REQUEST.getCode(), "Glue source is empty.");
            }
            jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));
        }

        // valid advanced
        if (Objects.isNull(jobInfo.getExecutorRouteStrategy())) {
            return R.fail(RCode.BAD_REQUEST.getCode(), "Empty executor route strategy.");
        }
        if (Objects.isNull(jobInfo.getMisfireStrategy())) {
            return R.fail(RCode.BAD_REQUEST.getCode(), "Empty misfire strategy.");
        }
        if (Objects.isNull(jobInfo.getExecutorBlockStrategy())) {
            return R.fail(RCode.BAD_REQUEST.getCode(), "Empty executor block strategy.");
        }
        //Validate the child job
        if (jobInfo.getChildJobId() != null && !jobInfo.getChildJobId().trim().isEmpty()) {
            String[] childJobIds = jobInfo.getChildJobId().split(",");
            for (String childJobIdItem : childJobIds) {
                if (childJobIdItem != null && !childJobIdItem.trim().isEmpty() && isNumeric(childJobIdItem)) {
                    Optional<JobInfo> childJobInfo = jobInfoDao.findOne(Long.parseLong(childJobIdItem));
                    if (childJobInfo.isEmpty()) {
                        return R.fail(RCode.BAD_REQUEST.getCode(), "Child job id " + childJobIdItem + " not exist.");
                    }
                } else {
                    return R.fail(RCode.BAD_REQUEST.getCode(), "Child job id " + childJobIdItem + " is not valid.");
                }
            }
            // join , avoid "xxx,,"
            StringBuilder temp = new StringBuilder();
            for (String item : childJobIds) {
                temp.append(item).append(",");
            }
            temp = new StringBuilder(temp.substring(0, temp.length() - 1));
            jobInfo.setChildJobId(temp.toString());
        }
        return R.ok(jobInfo.getId());
    }

    private final Jdbi jdbi;
    private final JobInfoDao jobInfoDao;
    private final JobGroupDao jobGroupDao;
    private final JobLogDao jobLogInfoDao;
    private final JobLogGlueDao jobLogGlueDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);


}
