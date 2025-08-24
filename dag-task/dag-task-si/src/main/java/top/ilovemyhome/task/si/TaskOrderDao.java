package top.ilovemyhome.task.si;

import top.ilovemyhome.peanotes.commons.jdbi.dao.BaseDao;
import top.ilovemyhome.task.si.domain.TaskOrder;

import java.util.Optional;

public interface TaskOrderDao extends BaseDao<TaskOrder> {

    Optional<TaskOrder> findByKey(String key);

    int updateByKey(String key, TaskOrder task);

    int deleteByKey(String key);

}
