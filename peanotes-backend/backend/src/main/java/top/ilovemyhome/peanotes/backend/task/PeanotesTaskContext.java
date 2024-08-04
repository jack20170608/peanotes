package top.ilovemyhome.peanotes.backend.task;

import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.common.task.TaskContext;

public class PeanotesTaskContext extends TaskContext {

    private PeanotesTaskContext(AppContext appContext) {
        super(appContext.getDataSourceFactory().getJdbi());
    }

    private static PeanotesTaskContext INSTANCE = null;

    public synchronized static PeanotesTaskContext getInstance(AppContext appContext) {
        if (INSTANCE == null) {
            INSTANCE = new PeanotesTaskContext(appContext);
        }
        return INSTANCE;
    }


}
