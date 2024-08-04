package top.ilovemyhome.peanotes.backend.common.task.impl.execution;

import org.apache.commons.lang3.ThreadUtils;
import top.ilovemyhome.peanotes.backend.common.task.TaskExecution;
import top.ilovemyhome.peanotes.backend.common.task.TaskInput;
import top.ilovemyhome.peanotes.backend.common.task.TaskOutput;
import top.ilovemyhome.peanotes.backend.common.task.impl.StringTaskOutput;

import java.time.Duration;

public class LongRunningExecution implements TaskExecution<String, String> {

    @Override
    public TaskOutput<String> execute(TaskInput<String> input) {
        String in = input.getInput();
        ThreadUtils.sleepQuietly(Duration.ofSeconds(2));
        return StringTaskOutput.success(in + "->" + getClass().getSimpleName());
    }
}
