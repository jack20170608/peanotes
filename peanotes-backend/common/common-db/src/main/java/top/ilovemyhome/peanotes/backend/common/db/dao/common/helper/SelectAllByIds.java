package top.ilovemyhome.peanotes.backend.common.db.dao.common.helper;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SqlGenerator;

import java.util.Collection;
import java.util.List;

@FunctionalInterface
public interface SelectAllByIds<T> extends SelectAll<T> {

    default Collection<T> selectAllByIds(Class<T> clazz, Jdbi jdbi, List<Long> ids) {
        return jdbi.withHandle(getSelectByIdsCallback(clazz, ids));
    }

    default Collection<T> selectAllByIds(Class<T> clazz, Handle handle, List<Long> ids) {
        return getSelectByIdsCallback(clazz ,ids).withHandle(handle);
    }

    default HandleCallback<List<T>, RuntimeException> getSelectByIdsCallback(Class<T> clazz, List<Long> ids) {
        SqlGenerator sqlGenerator = new SqlGenerator();
        String sql = sqlGenerator.selectByIds(get(), 2);
        return handle -> (List<T>)handle.createQuery(sql)
            .bindList("ids", ids)
            .mapTo(clazz)
            .list();
    }
}
