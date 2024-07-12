package top.ilovemyhome.peanotes.backend.common.db.dao.common.helper;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SqlGenerator;

import java.util.List;

public interface CountAllByIds extends CountAll{

    default int countAllByIds(final Handle handle){
        return getCountAllCallback().withHandle(handle);
    }

    default int countAllByIds(final Jdbi jdbi){
        return jdbi.withHandle(getCountAllCallback());
    }

    default HandleCallback<Integer, RuntimeException> getCountAllByIdsCallback(List<Long> ids) {
        SqlGenerator sqlGenerator = new SqlGenerator();
        String sql = sqlGenerator.count(get()) + " and id in ( <ids> )";
        return handle -> handle.createQuery(sql)
            .bindList("ids", ids)
            .mapTo(Integer.class)
            .one();
    }
}
