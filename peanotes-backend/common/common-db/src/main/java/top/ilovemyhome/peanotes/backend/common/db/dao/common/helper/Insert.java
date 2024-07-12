package top.ilovemyhome.peanotes.backend.common.db.dao.common.helper;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SqlGenerator;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;

import java.util.function.Supplier;

@FunctionalInterface
public interface Insert<T> extends Supplier<TableDescription> {

    default Long insert(Jdbi jdbi, T t){
        return jdbi.withHandle(getInsertCallback(t));
    }

    default Long insert(Handle handle, T t){
        return getInsertCallback(t).withHandle(handle);
    }

    default HandleCallback<Long, RuntimeException> getInsertCallback(T t){
        SqlGenerator sqlGenerator = new SqlGenerator();
        String sql = sqlGenerator.create(get());
        return handle -> handle.createUpdate(sql)
            .bind("t", t)
            .executeAndReturnGeneratedKeys("id")
            .mapTo(Long.class)
            .one();
    }
}
