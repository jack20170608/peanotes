package top.ilovemyhome.peanotes.backend.common.task.impl;

import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.task.TaskContext;

public class FooTaskContext extends TaskContext {

    private FooTaskContext(Jdbi jdbi) {
        super(jdbi);
    }

    private static FooTaskContext INSTANCE ;

    public synchronized static FooTaskContext getInstance(Jdbi jdbi){
        if (INSTANCE == null) {
            INSTANCE = new FooTaskContext(jdbi);
        }
        return INSTANCE;
    }
}
