package top.ilovemyhome.task.core.execution;

import org.apache.commons.lang3.StringUtils;
import top.ilovemyhome.task.si.TaskExecution;
import top.ilovemyhome.task.si.TaskInput;
import top.ilovemyhome.task.si.TaskOutput;

public class ConditionExceptionalExecution implements TaskExecution<String, String> {

    @Override
    public TaskOutput<String> execute(TaskInput<String> input) {
        Long taskId = input.taskId();
        String in = input.input();
        if (StringUtils.startsWith(in, "error")){
            throw new RuntimeException("Mocked exception");
        }
        return TaskOutput.success(taskId, in + "->" +getClass().getSimpleName());
    }
}
