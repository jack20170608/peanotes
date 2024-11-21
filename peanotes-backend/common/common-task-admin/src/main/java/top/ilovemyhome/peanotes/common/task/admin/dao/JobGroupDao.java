package top.ilovemyhome.peanotes.common.task.admin.dao;

import top.ilovemyhome.peanotes.backend.common.db.dao.common.BaseDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobGroup;

import java.util.List;

public interface JobGroupDao extends BaseDao<JobGroup> {

    List<JobGroup> findByAddressType(int addressType);

}
