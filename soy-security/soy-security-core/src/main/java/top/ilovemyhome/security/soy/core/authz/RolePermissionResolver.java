package top.ilovemyhome.security.soy.core.authz;

import java.util.Collection;

/**
 * A RolePermissionResolver resolves a String value and converts it into a Collection of
 * {@link org.apache.shiro.authz.Permission} instances.
 * <p/>
 * In some cases a {@link org.apache.shiro.realm.Realm} my only be able to return a list of roles.  This
 * component allows an application to resolve the roles into permissions.
 */
@FunctionalInterface
public interface RolePermissionResolver {

    /**
     * Resolves a Collection of Permissions based on the given String representation.
     *
     * @param roleString the String representation of a role name to resolve.
     * @return a Collection of Permissions based on the given String representation.
     */
    Collection<Permission> resolvePermissionsInRole(String roleString);

}
