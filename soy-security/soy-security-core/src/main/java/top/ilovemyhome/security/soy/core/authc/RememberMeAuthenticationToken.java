package top.ilovemyhome.security.soy.core.authc;

/**
 * An {@code AuthenticationToken} that indicates if the user wishes their identity to be remembered across sessions.
 * <p/>
 * Note however that when a new session is created for the corresponding user, that user's identity would be
 * remembered, but they are <em>NOT</em> considered authenticated.  Please see the
 * {@link org.apache.shiro.subject.Subject#isRemembered()} JavaDoc for an in-depth explanation of the semantic
 * differences of what it means to be remembered vs. authenticated.
 *
 * @see org.apache.shiro.subject.Subject#isRemembered()
 * @since 0.9
 */
public interface RememberMeAuthenticationToken extends AuthenticationToken {

    /**
     * Returns {@code true} if the submitting user wishes their identity (principal(s)) to be remembered
     * across sessions, {@code false} otherwise.
     *
     * @return {@code true} if the submitting user wishes their identity (principal(s)) to be remembered
     * across sessions, {@code false} otherwise.
     */
    boolean isRememberMe();

}
