package top.ilovemyhome.peanotes.backend.dao;

import top.ilovemyhome.peanotes.backend.common.db.dao.common.BaseDao;
import top.ilovemyhome.peanotes.backend.domain.QueryResultV1;
import top.ilovemyhome.peanotes.backend.domain.QueryResultV2;

public interface QueryDao extends BaseDao<QueryResultV1> {
    QueryResultV1 queryV1(String sql);
    QueryResultV2 queryV2(String sql);
}
