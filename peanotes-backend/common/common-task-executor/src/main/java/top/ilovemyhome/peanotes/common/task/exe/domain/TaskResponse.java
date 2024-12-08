package top.ilovemyhome.peanotes.common.task.exe.domain;

public record TaskResponse(int code, String msg, String content) {

    public static TaskResponse of(int code, String msg, String content) {
        return new TaskResponse(code, msg, content);
    }

    public static TaskResponse ofSuccess(String msg){
        return new TaskResponse(200, msg, null);
    }

    public static final TaskResponse SUCCESS = TaskResponse.of(200, "OK", null);
    public static final TaskResponse CLIENT_ERROR = TaskResponse.of(400, "Client Error", null);
    public static final TaskResponse SERVER_ERROR = TaskResponse.of(500, "Internal Server Error", null);



}
