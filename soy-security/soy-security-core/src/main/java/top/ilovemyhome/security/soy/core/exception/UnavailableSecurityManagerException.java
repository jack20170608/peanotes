package top.ilovemyhome.security.soy.core.exception;


/**
 * Exception thrown when attempting to acquire the application's {@code SecurityManager} instance, but Shiro's
 * lookup heuristics cannot find one.  This typically indicates an invalid application configuration.
 *
 * @since 1.0
 */
public class UnavailableSecurityManagerException extends SoyException{

    public UnavailableSecurityManagerException(String message) {
        super(message);
    }

    /**
     * @deprecated This constructor is NOT used by Shiro directly, and will be removed in the future.
     */
    @Deprecated
    public UnavailableSecurityManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
