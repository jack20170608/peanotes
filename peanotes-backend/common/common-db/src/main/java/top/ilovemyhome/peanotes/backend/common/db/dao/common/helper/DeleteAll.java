package top.ilovemyhome.peanotes.backend.common.db.dao.common.helper;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SqlGenerator;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;

import java.util.function.Supplier;

@FunctionalInterface
public interface DeleteAll extends Supplier<TableDescription> {

    default int deleteAll(final Handle handle) {
        return getDeleteAllCallback().withHandle(handle);
    }

    default int deleteAll(final Jdbi jdbi){
        return jdbi.withHandle(getDeleteAllCallback());
    }

    default HandleCallback<Integer, RuntimeException> getDeleteAllCallback() {
        SqlGenerator sqlGenerator = new SqlGenerator();
        String sql = sqlGenerator.deleteAll(get());
        return handle -> handle.createUpdate(sql)
            .execute();
    }
}
