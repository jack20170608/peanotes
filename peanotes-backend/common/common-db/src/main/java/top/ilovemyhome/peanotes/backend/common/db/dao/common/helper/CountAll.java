package top.ilovemyhome.peanotes.backend.common.db.dao.common.helper;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SqlGenerator;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;

import java.util.function.Supplier;

@FunctionalInterface
public interface CountAll extends Supplier<TableDescription> {


    default int countAll(final Handle handle){
        return getCountAllCallback().withHandle(handle);
    }

    default int countAll(final Jdbi jdbi){
        return jdbi.withHandle(getCountAllCallback());
    }

    default HandleCallback<Integer, RuntimeException> getCountAllCallback() {
        SqlGenerator sqlGenerator = new SqlGenerator();
        String countAllSql = sqlGenerator.count(get());
        return handle -> handle.createQuery(countAllSql)
            .mapTo(Integer.class)
            .one();
    }

}
