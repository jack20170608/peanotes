package top.ilovemyhome.security.soy.core.exception;


public class SoyException extends RuntimeException {


    public SoyException() {
        super();
    }


    public SoyException(String message) {
        super(message);
    }


    public SoyException(Throwable cause) {
        super(cause);
    }

    public SoyException(String message, Throwable cause) {
        super(message, cause);
    }

}
