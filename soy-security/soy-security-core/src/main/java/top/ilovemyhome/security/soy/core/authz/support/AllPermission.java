package top.ilovemyhome.security.soy.core.authz.support;


import top.ilovemyhome.security.soy.core.authz.Permission;

import java.io.Serializable;


/**
 * An all <tt>AllPermission</tt> instance is one that always implies any other permission; that is, its
 * {@link #implies implies} method always returns <tt>true</tt>.
 *
 * <p>You should be very careful about the users, roles, and/or groups to which this permission is assigned since
 * those respective entities will have the ability to do anything.  As such, an instance of this class
 * is typically only assigned only to "root" or "administrator" users or roles.
 *
 * @since 0.1
 */
public class AllPermission implements Permission, Serializable {

    /**
     * Always returns <tt>true</tt>, indicating any Subject granted this permission can do anything.
     *
     * @param p the Permission to check for implies logic.
     * @return <tt>true</tt> always, indicating any Subject grated this permission can do anything.
     */
    public boolean implies(Permission p) {
        return true;
    }
}
