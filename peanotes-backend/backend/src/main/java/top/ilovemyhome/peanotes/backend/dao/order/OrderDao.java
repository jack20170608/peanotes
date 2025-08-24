package top.ilovemyhome.peanotes.backend.dao.order;

import top.ilovemyhome.peanotes.backend.common.db.dao.common.BaseDao;
import top.ilovemyhome.peanotes.backend.web.dto.common.Order;

import java.util.List;

public interface OrderDao extends BaseDao<Order> {
    List<Long> getNextIds(int size);
}
