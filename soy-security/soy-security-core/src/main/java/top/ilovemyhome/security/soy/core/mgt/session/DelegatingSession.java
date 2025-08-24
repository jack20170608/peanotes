package top.ilovemyhome.security.soy.core.mgt.session;


import top.ilovemyhome.security.soy.core.exception.session.InvalidSessionException;
import top.ilovemyhome.security.soy.core.session.Session;

import java.io.Serializable;
import java.util.Collection;

/**
 * A DelegatingSession is a client-tier representation of a server side
 * {@link org.apache.shiro.session.Session Session}.
 * This implementation is basically a proxy to a server-side {@link NativeSessionManager NativeSessionManager},
 * which will return the proper results for each method call.
 * <p/>
 * <p>A <tt>DelegatingSession</tt> will cache data when appropriate to avoid a remote method invocation,
 * only communicating with the server when necessary.
 * <p/>
 * <p>Of course, if used in-process with a NativeSessionManager business POJO, as might be the case in a
 * web-based application where the web classes and server-side business pojos exist in the same
 * JVM, a remote method call will not be incurred.
 *
 * @since 0.1
 */
public class DelegatingSession implements Session, Serializable {

    //TODO - complete JavaDoc

    private final SessionKey key;

    //cached fields to avoid a server-side method call if out-of-process:
    private long startTimestamp;
    private String host;

    /**
     * Handle to the target NativeSessionManager that will support the delegate calls.
     */
    private final transient NativeSessionManager sessionManager;


    public DelegatingSession(NativeSessionManager sessionManager, SessionKey key) {
        if (sessionManager == null) {
            throw new IllegalArgumentException("sessionManager argument cannot be null.");
        }
        if (key == null) {
            throw new IllegalArgumentException("sessionKey argument cannot be null.");
        }
        if (key.getSessionId() == null) {
            String msg = "The " + DelegatingSession.class.getName() + " implementation requires that the "
                    + "SessionKey argument returns a non-null sessionId to support the "
                    + "Session.getId() invocations.";
            throw new IllegalArgumentException(msg);
        }
        this.sessionManager = sessionManager;
        this.key = key;
    }

    /**
     * @see org.apache.shiro.session.Session#getId()
     */
    public Serializable getId() {
        return key.getSessionId();
    }

    /**
     * @see org.apache.shiro.session.Session#getStartTimestamp()
     */
    public long getStartTimestamp() {
        if (startTimestamp == 0L) {
            startTimestamp = sessionManager.getStartTimestamp(key);
        }
        return startTimestamp;
    }

    /**
     * @see org.apache.shiro.session.Session#getLastAccessTime()
     */
    public long getLastAccessTime() {
        //can't cache - only business pojo knows the accurate time:
        return sessionManager.getLastAccessTime(key);
    }

    public long getTimeout() throws InvalidSessionException {
        return sessionManager.getTimeout(key);
    }

    public void setTimeout(long maxIdleTimeInMillis) throws InvalidSessionException {
        sessionManager.setTimeout(key, maxIdleTimeInMillis);
    }

    public String getHost() {
        if (host == null) {
            host = sessionManager.getHost(key);
        }
        return host;
    }

    /**
     * @see org.apache.shiro.session.Session#touch()
     */
    public void touch() throws InvalidSessionException {
        sessionManager.touch(key);
    }

    /**
     * @see org.apache.shiro.session.Session#stop()
     */
    public void stop() throws InvalidSessionException {
        sessionManager.stop(key);
    }

    /**
     * @see org.apache.shiro.session.Session#getAttributeKeys
     */
    public Collection<Object> getAttributeKeys() throws InvalidSessionException {
        return sessionManager.getAttributeKeys(key);
    }

    /**
     * @see org.apache.shiro.session.Session#getAttribute(Object key)
     */
    public Object getAttribute(Object attributeKey) throws InvalidSessionException {
        return sessionManager.getAttribute(this.key, attributeKey);
    }

    /**
     * @see Session#setAttribute(Object key, Object value)
     */
    public void setAttribute(Object attributeKey, Object value) throws InvalidSessionException {
        if (value == null) {
            removeAttribute(attributeKey);
        } else {
            sessionManager.setAttribute(this.key, attributeKey, value);
        }
    }

    /**
     * @see Session#removeAttribute(Object key)
     */
    public Object removeAttribute(Object attributeKey) throws InvalidSessionException {
        return sessionManager.removeAttribute(this.key, attributeKey);
    }
}
