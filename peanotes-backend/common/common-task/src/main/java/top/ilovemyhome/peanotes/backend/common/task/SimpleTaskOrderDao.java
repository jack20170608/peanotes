package top.ilovemyhome.peanotes.backend.common.task;

import top.ilovemyhome.peanotes.backend.common.db.dao.common.BaseDao;

import java.util.Optional;

public interface SimpleTaskOrderDao extends BaseDao<SimpleTaskOrder> {

    Optional<SimpleTaskOrder> findByKey(String key);

    int updateByKey(String key, SimpleTaskOrder task);

    int deleteByKey(String key);

}
