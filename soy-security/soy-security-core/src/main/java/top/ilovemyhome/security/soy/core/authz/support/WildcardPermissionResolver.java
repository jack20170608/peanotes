package top.ilovemyhome.security.soy.core.authz.support;

import top.ilovemyhome.security.soy.core.authz.Permission;
import top.ilovemyhome.security.soy.core.authz.PermissionResolver;

/**
 * <tt>PermissionResolver</tt> implementation that returns a new {@link WildcardPermission WildcardPermission}
 * based on the input string.
 *
 * @since 0.9
 */
public class WildcardPermissionResolver implements PermissionResolver {
    boolean caseSensitive;

    /**
     * Constructor to specify case sensitivity for the resolved permissions.
     *
     * @param caseSensitive true if permissions should be case sensitive.
     */
    public WildcardPermissionResolver(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Default constructor.
     * Equivalent to calling WildcardPermissionResolver(false)
     *
     * @see WildcardPermissionResolver#WildcardPermissionResolver(boolean)
     */
    public WildcardPermissionResolver() {
        this(WildcardPermission.DEFAULT_CASE_SENSITIVE);
    }

    /**
     * Set the case sensitivity of the resolved Wildcard permissions.
     *
     * @param state the caseSensitive flag state for resolved permissions.
     */
    public void setCaseSensitive(boolean state) {
        this.caseSensitive = state;
    }

    /**
     * Return true if this resolver produces case sensitive permissions.
     *
     * @return true if this resolver produces case sensitive permissions.
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Returns a new {@link WildcardPermission WildcardPermission} instance constructed based on the specified
     * <tt>permissionString</tt>.
     *
     * @param permissionString the permission string to convert to a {@link Permission Permission} instance.
     * @return a new {@link WildcardPermission WildcardPermission} instance constructed based on the specified
     * <tt>permissionString</tt>
     */
    public Permission resolvePermission(String permissionString) {
        return new WildcardPermission(permissionString, caseSensitive);
    }
}
