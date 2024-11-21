package top.ilovemyhome.peanotes.common.task.admin.dao;

import top.ilovemyhome.peanotes.backend.common.db.dao.common.BaseDao;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Page;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobInfo;
import top.ilovemyhome.peanotes.common.task.admin.web.dto.JobInfoQueryDto;

import java.time.LocalDateTime;
import java.util.List;

public interface JobInfoDao extends BaseDao<JobInfo> {

    Page<JobInfo> query(JobInfoQueryDto queryDto);

    List<JobInfo> scheduleJobQuery(LocalDateTime maxNextTriggerTime, int recordSize);

    int scheduleUpdate(JobInfo jobInfo);
}
