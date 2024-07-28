package top.ilovemyhome.peanotes.backend.common.task.impl.execution;

import org.apache.commons.lang3.StringUtils;
import top.ilovemyhome.peanotes.backend.common.task.TaskExecution;
import top.ilovemyhome.peanotes.backend.common.task.TaskInput;
import top.ilovemyhome.peanotes.backend.common.task.TaskOutput;
import top.ilovemyhome.peanotes.backend.common.task.impl.StringTaskOutput;

public class ConditionExceptionalExecution implements TaskExecution<String, String> {

    @Override
    public TaskOutput<String> execute(TaskInput<String> input) {
        String in = input.getInput();
        if (StringUtils.startsWith(in, "error")){
            throw new RuntimeException("Mocked exception");
        }
        return StringTaskOutput.success(in + "->" +getClass().getSimpleName());
    }
}
