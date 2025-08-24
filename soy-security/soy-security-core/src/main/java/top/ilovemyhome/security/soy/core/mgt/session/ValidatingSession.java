package top.ilovemyhome.security.soy.core.mgt.session;


import top.ilovemyhome.security.soy.core.exception.session.InvalidSessionException;
import top.ilovemyhome.security.soy.core.session.Session;

/**
 * A <code>ValidatingSession</code> is a <code>Session</code> that is capable of determining it is valid or not and
 * is able to validate itself if necessary.
 * <p/>
 * Validation is usually an exercise of determining when the session was last accessed or modified and determining if
 * that time is longer than a specified allowed duration.
 *
 * @since 0.9
 */
public interface ValidatingSession extends Session {

    boolean isValid();

    void validate() throws InvalidSessionException;
}
