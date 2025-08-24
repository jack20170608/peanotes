package top.ilovemyhome.security.soy.core.authz;

/**
 * Interface implemented by a component that wishes to use any application-configured <tt>PermissionResolver</tt> that
 * might already exist instead of potentially creating one itself.
 *
 * <p>This is mostly implemented by {@link org.apache.shiro.authz.Authorizer Authorizer} and
 * {@link org.apache.shiro.realm.Realm Realm} implementations since they
 * are the ones performing permission checks and need to know how to resolve Strings into
 * {@link org.apache.shiro.authz.Permission Permission} instances.
 *
 * @since 0.9
 */
@FunctionalInterface
public interface PermissionResolverAware {

    /**
     * Sets the specified <tt>PermissionResolver</tt> on this instance.
     *
     * @param pr the <tt>PermissionResolver</tt> being set.
     */
    void setPermissionResolver(PermissionResolver pr);
}
