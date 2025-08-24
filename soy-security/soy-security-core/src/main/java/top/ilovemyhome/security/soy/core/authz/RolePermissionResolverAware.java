package top.ilovemyhome.security.soy.core.authz;

/**
 * Interface implemented by a component that wishes to use any application-configured <tt>RolePermissionResolver</tt> that
 * might already exist instead of potentially creating one itself.
 *
 * <p>This is mostly implemented by {@link org.apache.shiro.authz.Authorizer Authorizer} and
 * {@link org.apache.shiro.realm.Realm Realm} implementations since they
 * are the ones performing permission checks and need to know how to resolve Strings into
 * {@link org.apache.shiro.authz.Permission Permission} instances.
 *
 * @since 1.0
 */
@FunctionalInterface
public interface RolePermissionResolverAware {

    /**
     * Sets the specified <tt>RolePermissionResolver</tt> on this instance.
     *
     * @param rpr the <tt>RolePermissionResolver</tt> being set.
     */
    void setRolePermissionResolver(RolePermissionResolver rpr);
}
