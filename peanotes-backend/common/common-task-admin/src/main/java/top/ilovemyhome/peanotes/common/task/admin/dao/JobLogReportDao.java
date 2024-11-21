package top.ilovemyhome.peanotes.common.task.admin.dao;

import top.ilovemyhome.peanotes.backend.common.db.dao.common.BaseDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobLogReport;

import java.time.LocalDate;

public interface JobLogReportDao extends BaseDao<JobLogReport> {

    int updateByTriggerDate(LocalDate triggerDate, JobLogReport jobLogReport);
}
