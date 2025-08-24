package top.ilovemyhome.security.soy.core.authc.pam;

import top.ilovemyhome.security.soy.core.subject.PrincipalCollection;
import top.ilovemyhome.security.soy.core.authc.AuthenticationInfo;
import top.ilovemyhome.security.soy.core.authc.AuthenticationToken;
import top.ilovemyhome.security.soy.core.exception.authc.AuthenticationException;
import top.ilovemyhome.security.soy.core.exception.authc.ShortCircuitIterationException;
import top.ilovemyhome.security.soy.core.realm.Realm;

import java.util.Collection;

/**
 * {@link AuthenticationStrategy} implementation that only accepts the account data from
 * the first successfully consulted Realm and ignores all subsequent realms.  This is slightly
 * different behavior than {@link AtLeastOneSuccessfulStrategy}, so please review both to see
 * which one meets your needs better.
 *
 * @see AtLeastOneSuccessfulStrategy AtLeastOneSuccessfulAuthenticationStrategy
 * @since 0.9
 */
public class FirstSuccessfulStrategy extends AbstractAuthenticationStrategy {

    private boolean stopAfterFirstSuccess;

    public void setStopAfterFirstSuccess(boolean stopAfterFirstSuccess) {
        this.stopAfterFirstSuccess = stopAfterFirstSuccess;
    }

    public boolean getStopAfterFirstSuccess() {
        return stopAfterFirstSuccess;
    }

    /**
     * Returns {@code null} immediately, relying on this class's {@link #merge merge} implementation to return
     * only the first {@code info} object it encounters, ignoring all subsequent ones.
     */
    public AuthenticationInfo beforeAllAttempts(Collection<? extends Realm> realms, AuthenticationToken token)
            throws AuthenticationException {
        return null;
    }

    /**
     * Throws ShortCircuitIterationException if stopAfterFirstSuccess is set and authentication is
     * successful with a previously consulted realm.
     * Returns the <code>aggregate</code> method argument, without modification
     * otherwise.
     */
    public AuthenticationInfo beforeAttempt(Realm realm, AuthenticationToken token, AuthenticationInfo aggregate)
            throws AuthenticationException {
        if (getStopAfterFirstSuccess() && aggregate != null && !isEmpty(aggregate.getPrincipals())) {
            throw new ShortCircuitIterationException();
        }
        return aggregate;
    }

    private static boolean isEmpty(PrincipalCollection pc) {
        return pc == null || pc.isEmpty();
    }

    /**
     * Returns the specified {@code aggregate} instance if is non null and valid (that is, has principals and they are
     * not empty) immediately, or, if it is null or not valid, the {@code info} argument is returned instead.
     * <p/>
     * This logic ensures that the first valid info encountered is the one retained and all subsequent ones are ignored,
     * since this strategy mandates that only the info from the first successfully authenticated realm be used.
     */
    protected AuthenticationInfo merge(AuthenticationInfo info, AuthenticationInfo aggregate) {
        if (aggregate != null && !isEmpty(aggregate.getPrincipals())) {
            return aggregate;
        }
        return info != null ? info : aggregate;
    }
}
