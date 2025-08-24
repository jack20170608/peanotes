package top.ilovemyhome.task.core.execution;

import org.apache.commons.lang3.ThreadUtils;
import top.ilovemyhome.task.si.TaskExecution;
import top.ilovemyhome.task.si.TaskInput;
import top.ilovemyhome.task.si.TaskOutput;

import java.time.Duration;

public class LongRunningExecution implements TaskExecution<String, String> {

    @Override
    public TaskOutput<String> execute(TaskInput<String> input) {
        String in = input.input();
        Long taskId = input.taskId();
        ThreadUtils.sleepQuietly(Duration.ofSeconds(2));
        return TaskOutput.success(taskId, in + "->" + getClass().getSimpleName());
    }
}
