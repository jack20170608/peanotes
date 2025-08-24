package top.ilovemyhome.task.core;

import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.task.si.TaskContext;

public class FooTaskDagServiceImpl extends AbstractTaskDagServiceImpl<String, String> {

    public FooTaskDagServiceImpl(Jdbi jdbi, TaskContext<String, String> taskContext) {
        super(jdbi, taskContext);
    }
}
