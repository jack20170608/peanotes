package top.ilovemyhome.security.soy.core.mgt;

/**
 * Interface implemented by {@link SecurityManager} implementations that wrap another {@code SecurityManager} instance.
 */
public interface WrappedSecurityManager {
    /**
     * Returns the underlying {@code SecurityManager} instance that this instance wraps.
     *
     * @return instance
     * @param <SM> {@link SecurityManager} implementation type
     */
    <SM extends SecurityManager> SM unwrap();
}
