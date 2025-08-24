package top.ilovemyhome.task.core.execution;


import top.ilovemyhome.task.si.TaskExecution;
import top.ilovemyhome.task.si.TaskInput;
import top.ilovemyhome.task.si.TaskOutput;

public class PrintInputTaskExecution implements TaskExecution<String, String> {

    @Override
    public TaskOutput<String> execute(TaskInput<String> input) {
        logger.info("Input is [{}].", input);
        String in = input.input();
        return TaskOutput.success(input.taskId(), in + "->" + getClass().getSimpleName());
    }


}
