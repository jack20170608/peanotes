package top.ilovemyhome.peanotes.backend.common.db.dao.common;


import top.ilovemyhome.peanotes.backend.common.db.dao.page.Pageable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public interface SearchCriteria extends Serializable {

    String whereClause();

    default Map<String, Object> normalParams() {
        return Map.of();
    }

    default Map<String, List> listParam() {
        return Map.of();
    }


    default String pageableWhereClause(Pageable pageable) {
        return "";
    }

}