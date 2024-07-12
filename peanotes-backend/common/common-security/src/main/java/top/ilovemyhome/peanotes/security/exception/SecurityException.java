package top.ilovemyhome.peanotes.security.exception;

public class SecurityException extends RuntimeException {

    public SecurityException(int code) {
        this(code, "Security Exception", null);
    }

    public SecurityException(String message) {
        this(DEFAULT_CODE, message, null);
    }

    public SecurityException(int code, String message) {
        this(code, message, null);
    }

    public SecurityException(String message, Throwable cause) {
        this(DEFAULT_CODE, message, cause);
    }

    public SecurityException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }


    private int code = DEFAULT_CODE;

    private static final int DEFAULT_CODE = -1;

}
