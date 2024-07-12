package top.ilovemyhome.peanotes.backend.common.db.dao.common.helper;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SqlGenerator;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

@FunctionalInterface
public interface SelectAll<T> extends Supplier<TableDescription> {


    default Collection<T> selectAll(Class<T> clazz, final Handle handle) {
        return getSelectAllCallback(clazz).withHandle(handle);
    }

    default Collection<T> selectAll(Class<T> clazz, final Jdbi jdbi){
        return jdbi.withHandle(getSelectAllCallback(clazz));
    }

    default HandleCallback<List<T>, RuntimeException> getSelectAllCallback(Class<T> clazz) {
        SqlGenerator sqlGenerator = new SqlGenerator();
        String selectAllSql = sqlGenerator.selectAll(get());
        return handle -> (List<T>)handle.createQuery(selectAllSql)
            .mapTo(clazz)
            .list();
    }

}
