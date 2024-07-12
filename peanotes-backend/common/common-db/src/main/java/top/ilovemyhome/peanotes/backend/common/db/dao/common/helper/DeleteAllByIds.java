package top.ilovemyhome.peanotes.backend.common.db.dao.common.helper;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SqlGenerator;

import java.util.List;

@FunctionalInterface
public interface DeleteAllByIds extends DeleteAll {

    default int deleteAllByIds(Jdbi jdbi, final List<Long> ids) {
        return jdbi.withHandle(getDeleteByIdsCallback(ids));
    }

    default int deleteAllByIds(Handle handle, final List<Long> ids) {
        return getDeleteByIdsCallback(ids).withHandle(handle);
    }

    default HandleCallback<Integer, RuntimeException> getDeleteByIdsCallback(List<Long> ids) {
        SqlGenerator sqlGenerator = new SqlGenerator();
        String sql = sqlGenerator.deleteByIds(get());
        return handle -> handle.createUpdate(sql)
            .bindList("ids", ids)
            .execute();
    }
}
