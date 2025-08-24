package top.ilovemyhome.security.soy.core.exception.authc;

/**
 * Exception thrown due to a problem with the credential(s) submitted for an
 * account during the authentication process.
 *
 * @since 0.1
 */
public class CredentialsException extends AuthenticationException {

    /**
     * Creates a new CredentialsException.
     */
    public CredentialsException() {
        super();
    }

    /**
     * Constructs a new CredentialsException.
     *
     * @param message the reason for the exception
     */
    public CredentialsException(String message) {
        super(message);
    }

    /**
     * Constructs a new CredentialsException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public CredentialsException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new CredentialsException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public CredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

}
