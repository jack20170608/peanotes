package top.ilovemyhome.peanotes.backend.task.execution;

import top.ilovemyhome.peanotes.backend.common.task.TaskExecution;
import top.ilovemyhome.peanotes.backend.common.task.TaskInput;
import top.ilovemyhome.peanotes.backend.common.task.TaskOutput;
import top.ilovemyhome.peanotes.backend.common.task.impl.StringTaskOutput;

public class PrintInputTaskExecution implements TaskExecution<String, String> {

    @Override
    public TaskOutput<String> execute(TaskInput<String> input) {
        LOGGER.info("Input is [{}].", input);
        String in = input.getInput();
        return StringTaskOutput.success(in + "->" + getClass().getSimpleName());
    }
}
