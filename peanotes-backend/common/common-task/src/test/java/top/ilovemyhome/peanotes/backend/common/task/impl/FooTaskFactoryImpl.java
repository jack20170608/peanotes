package top.ilovemyhome.peanotes.backend.common.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.task.*;

public class FooTaskFactoryImpl implements TaskFactory {

    public FooTaskFactoryImpl(TaskContext taskContext) {
        taskContext.setTaskFactory(this);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FooTaskFactoryImpl.class);
}
