package top.ilovemyhome.peanotes.backend.common.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FunctionalInterface
public interface TaskExecution<I, O> {

    Logger LOGGER = LoggerFactory.getLogger(TaskExecution.class);

    TaskOutput<O> execute(TaskInput<I> input);
}
