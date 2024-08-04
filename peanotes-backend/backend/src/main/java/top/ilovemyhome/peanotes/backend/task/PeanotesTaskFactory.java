package top.ilovemyhome.peanotes.backend.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.task.*;
import top.ilovemyhome.peanotes.backend.application.AppContext;

public class PeanotesTaskFactory implements TaskFactory {

    public PeanotesTaskFactory(AppContext appContext) {
        this.taskContext = appContext.getTaskContext();
        taskContext.setTaskFactory(this);
    }

    private final TaskContext taskContext;
    private static final Logger LOGGER = LoggerFactory.getLogger(PeanotesTaskFactory.class);
}
