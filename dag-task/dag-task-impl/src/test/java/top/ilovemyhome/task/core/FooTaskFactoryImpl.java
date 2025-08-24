package top.ilovemyhome.task.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.task.si.TaskContext;
import top.ilovemyhome.task.si.TaskFactory;

public class FooTaskFactoryImpl implements TaskFactory {

    public FooTaskFactoryImpl(TaskContext<String, String> taskContext) {
        taskContext.setTaskFactory(this);
    }

    private static final Logger logger = LoggerFactory.getLogger(FooTaskFactoryImpl.class);
}
