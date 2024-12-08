package top.ilovemyhome.peanotes.common.task.exe;

import static top.ilovemyhome.peanotes.common.task.exe.processor.TaskProcessor.log;

public interface TaskHandler {


    default void handle() {
        log("Start handle...");
        doHandle();
        log("Handle successfully...");
    }


    void doHandle();

    default void init(){}

    default void destroy(){}

}
