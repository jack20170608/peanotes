package top.ilovemyhome.peanotes.common.task.admin.service;

import top.ilovemyhome.peanotes.backend.common.db.dao.page.Page;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobInfo;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobUser;
import top.ilovemyhome.peanotes.common.task.admin.web.dto.JobInfoQueryDto;
import top.ilovemyhome.peanotes.common.task.admin.web.handlers.helper.R;

public interface JobService {

    Page<JobInfo> query(JobInfoQueryDto queryDto);

    R<Long> add(JobInfo jobInfo);

    R<Long> update(JobInfo jobInfo);

    R<Long> remove(Long id);

    R<Long> start(Long id);

    R<Long> stop(Long id);

    R<Long> trigger(JobUser loginUser, Long jobId, String executorParam, String addressList);
}
