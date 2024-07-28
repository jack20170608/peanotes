package top.ilovemyhome.peanotes.backend.common.task;

import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.task.impl.FooTaskFactoryImpl;
import top.ilovemyhome.peanotes.backend.common.task.impl.StringTaskInput;

import java.util.Map;

public class FooTaskFactoryTest {

    @Test
    public void testCreateTask() {
        FooTaskFactoryImpl fooTaskFactory = new FooTaskFactoryImpl(null);
        TaskExecution<String, String> taskExecution
            = fooTaskFactory.createTaskForExecution("top.ilovemyhome.peanotes.backend.common.task.impl.execution.PrintInputTaskExecution");
        taskExecution.execute(new StringTaskInput(null, "input_xxxx", Map.of("k1", "v1", "k2", "v2")));
    }
}
