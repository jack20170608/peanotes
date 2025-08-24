package top.ilovemyhome.peanotes.commons.jdbi;


import top.ilovemyhome.peanotes.commons.jdbi.page.Pageable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public interface SearchCriteria extends Serializable {

    String whereClause();

    default Map<String, Object> normalParams() {
        return null;
    }

    default Map<String, List> listParam() {
        return null;
    }

    default String pageableWhereClause(Pageable pageable) {
        return null;
    }

}
