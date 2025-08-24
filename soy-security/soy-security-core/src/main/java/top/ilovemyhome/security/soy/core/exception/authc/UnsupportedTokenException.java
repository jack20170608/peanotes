package top.ilovemyhome.security.soy.core.exception.authc;

/**
 * Exception thrown during the authentication process when an
 * {@link org.apache.shiro.authc.AuthenticationToken AuthenticationToken} implementation is encountered that is not
 * supported by one or more configured {@link org.apache.shiro.realm.Realm Realm}s.
 *
 * @see org.apache.shiro.authc.pam.AuthenticationStrategy
 * @since 0.2
 */
public class UnsupportedTokenException extends AuthenticationException {

    /**
     * Creates a new UnsupportedTokenException.
     */
    public UnsupportedTokenException() {
        super();
    }

    /**
     * Constructs a new UnsupportedTokenException.
     *
     * @param message the reason for the exception
     */
    public UnsupportedTokenException(String message) {
        super(message);
    }

    /**
     * Constructs a new UnsupportedTokenException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public UnsupportedTokenException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UnsupportedTokenException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public UnsupportedTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
