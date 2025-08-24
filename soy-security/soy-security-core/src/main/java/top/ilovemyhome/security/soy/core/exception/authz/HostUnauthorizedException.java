package top.ilovemyhome.security.soy.core.exception.authz;

/**
 * Thrown when a particular client (that is, host address) has not been enabled to access the system
 * or if the client has been enabled access but is not permitted to perform a particular operation
 * or access a particular resource.
 *
 * @since 0.1
 */
public class HostUnauthorizedException extends UnauthorizedException {

    private String host;

    /**
     * Creates a new HostUnauthorizedException.
     */
    public HostUnauthorizedException() {
        super();
    }

    /**
     * Constructs a new HostUnauthorizedException.
     *
     * @param message the reason for the exception
     */
    public HostUnauthorizedException(String message) {
        super(message);
    }

    /**
     * Constructs a new HostUnauthorizedException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public HostUnauthorizedException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new HostUnauthorizedException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public HostUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Returns the host associated with this exception.
     *
     * @return the host associated with this exception.
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Sets the host associated with this exception.
     *
     * @param host the host associated with this exception.
     */
    public void setHostAddress(String host) {
        this.host = host;
    }
}
