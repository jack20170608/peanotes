package top.ilovemyhome.security.soy.core.exception.session;

/**
 * Exception thrown if attempting to create a new {@code Subject}
 * {@link org.apache.shiro.subject.Subject#getSession() session}, but that {@code Subject}'s sessions are disabled.
 * <p/>
 * Note that this exception represents an invalid API usage scenario - where Shiro has been configured to disable
 * sessions for a particular subject, but a developer is attempting to use that Subject's session.
 * <p/>
 * In other words, if this exception is encountered, it should be resolved by a configuration change for Shiro and
 * <em>not</em> by checking every Subject to see if they are enabled or not (which would likely introduce very
 * ugly/paranoid code checks everywhere a session is needed). This is why there is no
 * {@code subject.isSessionEnabled()} method.
 *
 * @since 1.2
 */
public class DisabledSessionException extends SessionException {

    public DisabledSessionException(String message) {
        super(message);
    }
}
