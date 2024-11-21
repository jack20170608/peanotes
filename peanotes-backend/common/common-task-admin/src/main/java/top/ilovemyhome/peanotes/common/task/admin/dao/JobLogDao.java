package top.ilovemyhome.peanotes.common.task.admin.dao;

import top.ilovemyhome.peanotes.backend.common.db.dao.common.BaseDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface JobLogDao extends BaseDao<JobLog> {

    int deleteByJobId(Long jobId);

    List<Long> findLostJobIds(LocalDateTime losedTime);

    int updateHandleInfo(JobLog xxlJobLog);

    Map<String, Object> findLogReport(LocalDateTime fromDt, LocalDateTime toDt);

    List<Long> findFailJobLogIds(Long id);

    int updateAlarmStatus(long failLogId, int i, int i1);
}
