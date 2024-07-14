package top.ilovemyhome.peanotes.backend.common.db.dao.common;

import java.util.List;
import java.util.Map;

public class FooSearchCriteria implements SearchCriteria {


    @Override
    public String whereClause() {
        return " where 1 =1 and id in <:listOfIds> and foo = :foo " +
            " and bar = :bar";
    }

    @Override
    public Map<String, Object> normalParams() {
        return Map.of("foo", "foo", "bar", "bar");
    }

    @Override
    public Map<String, List> listParam() {
        return Map.of("listOfIds", List.of(1,2,3));
    }
}
