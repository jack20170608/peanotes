package top.ilovemyhome.peanotes.backend.common.db.dao.jdbi;

import org.jdbi.v3.core.*;
import org.jdbi.v3.core.spi.JdbiPlugin;

import java.sql.SQLException;

public class ExampleJdbiPlugin implements JdbiPlugin, HandleCallbackDecorator {

    @Override
    public void customizeJdbi(Jdbi jdbi) {
        jdbi.setHandleCallbackDecorator(this);
    }

    @Override
    public <R, X extends Exception> HandleCallback<R, X> decorate(HandleCallback<R, X> callback) {
        return handle -> {
            try {
                if (handle.getConnection().getAutoCommit()) {
                    // do retries for auto-commit
                    return withRetry(handle, callback);
                }else {
                    //apply transaction support

                }
            } catch (SQLException e) {
                throw new ConnectionException(e);
            }

            // all others get standard behavior
            return callback.withHandle(handle);
        };
    }

    private <R, X extends Exception> R withRetry(Handle handle, HandleCallback<R, X> callback) throws X {
        while (true) {
            try {
                return callback.withHandle(handle);
            } catch (Exception last) {
                // custom auto-commit retry behavior goes here
            }
        }
    }
}
