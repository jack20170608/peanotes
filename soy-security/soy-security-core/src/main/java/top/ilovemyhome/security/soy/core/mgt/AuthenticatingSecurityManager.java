package top.ilovemyhome.security.soy.core.mgt;


import top.ilovemyhome.commons.common.lifecycle.LifecycleUtils;
import top.ilovemyhome.security.soy.core.authc.AuthenticationInfo;
import top.ilovemyhome.security.soy.core.authc.AuthenticationToken;
import top.ilovemyhome.security.soy.core.authc.Authenticator;
import top.ilovemyhome.security.soy.core.authc.pam.ModularRealmAuthenticator;
import top.ilovemyhome.security.soy.core.exception.authc.AuthenticationException;

/**
 * Shiro support of a {@link java.lang.SecurityManager} class hierarchy that delegates all
 * authentication operations to a wrapped {@link Authenticator Authenticator} instance.  That is, this class
 * implements all the <tt>Authenticator</tt> methods in the {@link java.lang.SecurityManager SecurityManager}
 * interface, but in reality, those methods are merely passthrough calls to the underlying 'real'
 * <tt>Authenticator</tt> instance.
 *
 * <p>All other <tt>SecurityManager</tt> (authorization, session, etc.) methods are left to be implemented by subclasses.
 *
 * <p>In keeping with the other classes in this hierarchy and Shiro's desire to minimize configuration whenever
 * possible, suitable default instances for all dependencies are created upon instantiation.
 *
 * @since 0.9
 */
public abstract class AuthenticatingSecurityManager extends RealmSecurityManager {

    /**
     * The internal <code>Authenticator</code> delegate instance that this SecurityManager instance will use
     * to perform all authentication operations.
     */
    private Authenticator authenticator;

    /**
     * Default no-arg constructor that initializes its internal
     * <code>authenticator</code> instance to a
     * {@link org.apache.shiro.authc.pam.ModularRealmAuthenticator ModularRealmAuthenticator}.
     */
    public AuthenticatingSecurityManager() {
        super();
        this.authenticator = new ModularRealmAuthenticator();
    }

    /**
     * Returns the delegate <code>Authenticator</code> instance that this SecurityManager uses to perform all
     * authentication operations.  Unless overridden by the
     * {@link #setAuthenticator(org.apache.shiro.authc.Authenticator) setAuthenticator}, the default instance is a
     * {@link org.apache.shiro.authc.pam.ModularRealmAuthenticator ModularRealmAuthenticator}.
     *
     * @return the delegate <code>Authenticator</code> instance that this SecurityManager uses to perform all
     * authentication operations.
     */
    public Authenticator getAuthenticator() {
        return authenticator;
    }

    /**
     * Sets the delegate <code>Authenticator</code> instance that this SecurityManager uses to perform all
     * authentication operations.  Unless overridden by this method, the default instance is a
     * {@link org.apache.shiro.authc.pam.ModularRealmAuthenticator ModularRealmAuthenticator}.
     *
     * @param authenticator the delegate <code>Authenticator</code> instance that this SecurityManager will use to
     *                      perform all authentication operations.
     * @throws IllegalArgumentException if the argument is <code>null</code>.
     */
    public void setAuthenticator(Authenticator authenticator) throws IllegalArgumentException {
        if (authenticator == null) {
            String msg = "Authenticator argument cannot be null.";
            throw new IllegalArgumentException(msg);
        }
        this.authenticator = authenticator;
    }

    /**
     * Passes on the {@link #getRealms() realms} to the internal delegate <code>Authenticator</code> instance so
     * that it may use them during authentication attempts.
     */
    protected void afterRealmsSet() {
        super.afterRealmsSet();
        if (this.authenticator instanceof ModularRealmAuthenticator) {
            ((ModularRealmAuthenticator) this.authenticator).setRealms(getRealms());
        }
    }

    /**
     * Delegates to the wrapped {@link org.apache.shiro.authc.Authenticator Authenticator} for authentication.
     */
    public AuthenticationInfo authenticate(AuthenticationToken token) throws AuthenticationException {
        return this.authenticator.authenticate(token);
    }

    public void destroy() {
        LifecycleUtils.destroy(getAuthenticator());
        this.authenticator = null;
        super.destroy();
    }
}
