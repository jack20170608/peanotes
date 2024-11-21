package top.ilovemyhome.peanotes.common.task.admin.dao;

import top.ilovemyhome.peanotes.backend.common.db.dao.common.BaseDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobLogGlue;

public interface JobLogGlueDao extends BaseDao<JobLogGlue> {

    int deleteByJobId(Long jobId);
}
