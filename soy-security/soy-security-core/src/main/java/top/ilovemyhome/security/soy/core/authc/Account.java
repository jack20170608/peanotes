package top.ilovemyhome.security.soy.core.authc;


import top.ilovemyhome.security.soy.core.authz.AuthorizationInfo;

/**
 * An <tt>Account</tt> is a convenience interface that extends both {@link AuthenticationInfo} and
 * {@link AuthorizationInfo} and represents authentication and authorization for a <em>single account</em> in a
 * <em>single Realm</em>.
 * <p/>
 * This interface can be useful when a Realm implementation finds it more convenient to use a single object to
 * encapsulate both the authentication and authorization information used by both authc and authz operations.
 * <p/>
 * <b>Please Note</b>:  Since Shiro sometimes logs account operations, please ensure your Account's <code>toString()</code>
 * implementation does <em>not</em> print out account credentials (password, etc.), as these might be viewable to
 * someone reading your logs.  This is good practice anyway, and account principals should rarely (if ever) be printed
 * out for any reason.  If you're using Shiro's default implementations of this interface, they only ever print the
 * account {@link #getPrincipals() principals}, so you do not need to do anything additional.
 *
 * @see SimpleAccount
 * @since 0.9
 */
public interface Account extends AuthenticationInfo, AuthorizationInfo {

}
