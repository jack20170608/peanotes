/**
 * Support for <em>PAM</em>, or <b>P</b>luggable <b>A</b>uthentication <b>M</b>odules, which is
 * the capability to authenticate a user against multiple configurable (pluggable) <em>modules</em> (Shiro
 * calls these {@link org.apache.shiro.realm.Realm Realm}s).
 * <p/>
 * The primary class of interest here is the
 *      {@link org.apache.shiro.authc.pam.ModularRealmAuthenticator ModularRealmAuthenticator}
 * * which is an <code>Authenticator</code> implementation that coordinates authentication attempts across
 * one or more Realm instances.
 * <p/>
 * How the <code>ModularRealmAuthenticator</code> actually coordinates this behavior is configurable based on your
 * application's needs using an injectable
 * {@link org.apache.shiro.authc.pam.AuthenticationStrategy}.
 */
package top.ilovemyhome.security.soy.core.authc.pam;
