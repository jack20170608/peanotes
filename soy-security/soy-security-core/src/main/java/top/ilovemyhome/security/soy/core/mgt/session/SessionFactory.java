package top.ilovemyhome.security.soy.core.mgt.session;

import top.ilovemyhome.security.soy.core.session.Session;

/**
 * A simple factory class that instantiates concrete {@link Session Session} instances.  This is mainly a
 * mechanism to allow instances to be created at runtime if they need to be different the
 * defaults.  It is not used by end-users of the framework, but rather those configuring Shiro to work in an
 * application, and is typically injected into the {@link org.apache.shiro.mgt.SecurityManager SecurityManager} or a
 * {@link SessionManager}.
 *
 * @since 1.0
 */
public interface SessionFactory {

    /**
     * Creates a new {@code Session} instance based on the specified contextual initialization data.
     *
     * @param initData the initialization data to be used during {@link Session} creation.
     * @return a new {@code Session} instance.
     * @since 1.0
     */
    Session createSession(SessionContext initData);
}
