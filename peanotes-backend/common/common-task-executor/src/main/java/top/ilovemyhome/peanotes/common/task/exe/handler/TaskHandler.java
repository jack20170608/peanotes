package top.ilovemyhome.peanotes.common.task.exe.handler;

import top.ilovemyhome.peanotes.common.task.exe.TaskContext;


public interface TaskHandler {


    default void handle() {
        doHandle();
    }

    void doHandle();

    default void init(){}

    default void destroy(){}

}
