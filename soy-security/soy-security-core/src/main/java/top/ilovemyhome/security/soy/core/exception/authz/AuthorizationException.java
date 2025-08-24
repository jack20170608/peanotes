package top.ilovemyhome.security.soy.core.exception.authz;


import top.ilovemyhome.security.soy.core.exception.SoyException;

/**
 * Exception thrown if there is a problem during authorization (access control check).
 *
 * @since 0.1
 */
public class AuthorizationException extends SoyException {

    public AuthorizationException() {
        super();
    }


    public AuthorizationException(String message) {
        super(message);
    }


    public AuthorizationException(Throwable cause) {
        super(cause);
    }


    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
