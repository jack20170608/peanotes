package top.ilovemyhome.peanotes.backend.common.task.persistent;

import top.ilovemyhome.peanotes.backend.common.db.dao.common.BaseDao;

import java.util.Optional;

public interface TaskOrderDao extends BaseDao<TaskOrder> {

    Optional<TaskOrder> findByKey(String key);

    int updateByKey(String key, TaskOrder task);

    int deleteByKey(String key);

}
