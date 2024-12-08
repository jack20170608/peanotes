package top.ilovemyhome.peanotes.common.task.exe;

public class TaskExecuteException extends RuntimeException{

    public TaskExecuteException(String message) {
        super(message);
    }

    public TaskExecuteException(String message, Throwable cause) {
        super(message, cause);
    }
}
