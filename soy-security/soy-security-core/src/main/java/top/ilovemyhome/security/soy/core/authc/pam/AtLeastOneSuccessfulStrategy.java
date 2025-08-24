package top.ilovemyhome.security.soy.core.authc.pam;


import top.ilovemyhome.security.soy.core.subject.PrincipalCollection;
import top.ilovemyhome.security.soy.core.authc.AuthenticationInfo;
import top.ilovemyhome.security.soy.core.authc.AuthenticationToken;
import top.ilovemyhome.security.soy.core.exception.authc.AuthenticationException;

/**
 * <tt>AuthenticationStrategy</tt> implementation that requires <em>at least one</em> configured realm to
 * successfully process the submitted <tt>AuthenticationToken</tt> during the log-in attempt.
 * <p/>
 * <p>This means any number of configured realms do not have to support the submitted log-in token, or they may
 * be unable to acquire <tt>AuthenticationInfo</tt> for the token, but as long as at least one can do both, this
 * Strategy implementation will allow the log-in process to be successful.
 * <p/>
 * <p>Note that this implementation will aggregate the account data from <em>all</em> successfully consulted
 * realms during the authentication attempt. If you want only the account data from the first successfully
 * consulted realm and want to ignore all subsequent realms, use the
 * {@link FirstSuccessfulStrategy FirstSuccessfulAuthenticationStrategy} instead.
 *
 * @see FirstSuccessfulStrategy FirstSuccessfulAuthenticationStrategy
 * @since 0.2
 */
public class AtLeastOneSuccessfulStrategy extends AbstractAuthenticationStrategy {

    private static boolean isEmpty(PrincipalCollection pc) {
        return pc == null || pc.isEmpty();
    }

    /**
     * Ensures that the <code>aggregate</code> method argument is not <code>null</code> and
     * <code>aggregate.{@link org.apache.shiro.authc.AuthenticationInfo#getPrincipals() getPrincipals()}</code>
     * is not <code>null</code>, and if either is <code>null</code>, throws an AuthenticationException to indicate
     * that none of the realms authenticated successfully.
     */
    public AuthenticationInfo afterAllAttempts(AuthenticationToken token, AuthenticationInfo aggregate)
            throws AuthenticationException {
        //we know if one or more were able to successfully authenticate if the aggregated account object does not
        //contain null or empty data:
        if (aggregate == null || isEmpty(aggregate.getPrincipals())) {
            throw new AuthenticationException("Authentication token of type [" + token.getClass() + "] "
                    + "could not be authenticated by any configured realms.  Please ensure that at least one realm can "
                    + "authenticate these tokens.");
        }

        return aggregate;
    }
}
