package top.ilovemyhome.security.soy.core.mgt.session;


import top.ilovemyhome.security.soy.core.exception.session.InvalidSessionException;
import top.ilovemyhome.security.soy.core.session.ProxiedSession;
import top.ilovemyhome.security.soy.core.session.Session;

/**
 * Implementation of the {@link Session Session} interface that proxies another <code>Session</code>, but does not
 * allow any 'write' operations to the underlying session. It allows 'read' operations only.
 * <p/>
 * The <code>Session</code> write operations are defined as follows.  A call to any of these methods on this
 * proxy will immediately result in an {@link InvalidSessionException} being thrown:
 * <ul>
 * <li>{@link Session#setTimeout(long) Session.setTimeout(long)}</li>
 * <li>{@link Session#touch() Session.touch()}</li>
 * <li>{@link Session#stop() Session.stop()}</li>
 * <li>{@link Session#setAttribute(Object, Object) Session.setAttribute(key,value)}</li>
 * <li>{@link Session#removeAttribute(Object) Session.removeAttribute(key)}</li>
 * </ul>
 * Any other method invocation not listed above will result in a corresponding call to the underlying <code>Session</code>.
 *
 * @since 0.9
 */
public class ImmutableProxiedSession extends ProxiedSession {

    /**
     * Constructs a new instance of this class proxying the specified <code>Session</code>.
     *
     * @param target the target <code>Session</code> to proxy.
     */
    public ImmutableProxiedSession(Session target) {
        super(target);
    }

    /**
     * Simply throws an <code>InvalidSessionException</code> indicating that this proxy is immutable.  Used
     * only in the Session's 'write' methods documented in the top class-level JavaDoc.
     *
     * @throws InvalidSessionException in all cases - used by the Session 'write' method implementations.
     */
    protected void throwImmutableException() throws InvalidSessionException {
        String msg = "This session is immutable and read-only - it cannot be altered.  This is usually because "
                + "the session has been stopped or expired already.";
        throw new InvalidSessionException(msg);
    }

    /**
     * Immediately {@link #throwImmutableException() throws} an <code>InvalidSessionException</code> in all
     * cases because this proxy is immutable.
     */
    public void setTimeout(long maxIdleTimeInMillis) throws InvalidSessionException {
        throwImmutableException();
    }

    /**
     * Immediately {@link #throwImmutableException() throws} an <code>InvalidSessionException</code> in all
     * cases because this proxy is immutable.
     */
    public void touch() throws InvalidSessionException {
        throwImmutableException();
    }

    /**
     * Immediately {@link #throwImmutableException() throws} an <code>InvalidSessionException</code> in all
     * cases because this proxy is immutable.
     */
    public void stop() throws InvalidSessionException {
        throwImmutableException();
    }

    /**
     * Immediately {@link #throwImmutableException() throws} an <code>InvalidSessionException</code> in all
     * cases because this proxy is immutable.
     */
    public void setAttribute(Object key, Object value) throws InvalidSessionException {
        throwImmutableException();
    }

    /**
     * Immediately {@link #throwImmutableException() throws} an <code>InvalidSessionException</code> in all
     * cases because this proxy is immutable.
     */
    public Object removeAttribute(Object key) throws InvalidSessionException {
        throwImmutableException();
        //we should never ever reach this point due to the exception being thrown.
        throw new InternalError("This code should never execute - please report this as a bug!");
    }
}
