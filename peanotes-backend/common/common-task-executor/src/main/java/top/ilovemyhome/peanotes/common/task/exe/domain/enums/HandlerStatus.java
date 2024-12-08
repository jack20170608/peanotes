package top.ilovemyhome.peanotes.common.task.exe.domain.enums;

public enum HandlerStatus {

    OK(200, "OK"),
    CLIENT_ERROR(400, "Client Error"),
    SERVER_ERROR(500, "Server Error"),
    HANDLE_CODE_TIMEOUT(502, "Server Error");
    private int code;
    private String message;

    HandlerStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }
}
