package top.ilovemyhome.peanotes.common.task.exe;

public interface TaskHandler {

    void handle() throws Exception;

    default void init() throws Exception {
        //do nothing
    }

    default void destroy() throws Exception {
        //do nothing
    }
}
