package top.ilovemyhome.commons.database.example.callback;

import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlywayPvtCallBack implements Callback {
    @Override
    public boolean supports(Event event, Context context) {
        return true;
    }

    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return false;
    }

    @Override
    public void handle(Event event, Context context) {
        logger.info("FlywayPvtCallBack.handle with event {}.", event);
    }

    @Override
    public String getCallbackName() {
        return "FlywayPvtCallBack";
    }

    private static final Logger logger = LoggerFactory.getLogger(FlywayPvtCallBack.class.getName());
}
