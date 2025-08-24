package top.ilovemyhome.security.soy.core.authc;


import top.ilovemyhome.security.soy.core.subject.PrincipalCollection;

/**
 * An SPI interface allowing cleanup logic to be executed during logout of a previously authenticated Subject/user.
 *
 * <p>As it is an SPI interface, it is really intended for SPI implementers such as those implementing Realms.
 *
 * <p>All of Shiro's concrete Realm implementations implement this interface as a convenience for those wishing
 * to subclass them.
 *
 * @since 0.9
 */
public interface LogoutAware {

    /**
     * Callback triggered when a <code>Subject</code> logs out of the system.
     *
     * @param principals the identifying principals of the Subject logging out.
     */
    void onLogout(PrincipalCollection principals);
}
