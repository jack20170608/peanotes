package top.ilovemyhome.security.soy.core.session;

import top.ilovemyhome.security.soy.core.exception.session.InvalidSessionException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;

/**
 * Simple <code>Session</code> implementation that immediately delegates all corresponding calls to an
 * underlying proxied session instance.
 * <p/>
 * This class is mostly useful for framework subclassing to intercept certain <code>Session</code> calls
 * and perform additional logic.
 *
 * @since 0.9
 */
public class ProxiedSession implements Session {

    /**
     * The proxied instance
     */
    protected final Session delegate;

    /**
     * Constructs an instance that proxies the specified <code>target</code>.  Subclasses may access this
     * target via the <code>protected final 'delegate'</code> attribute, i.e. <code>this.delegate</code>.
     *
     * @param target the specified target <code>Session</code> to proxy.
     */
    public ProxiedSession(Session target) {
        if (target == null) {
            throw new IllegalArgumentException("Target session to proxy cannot be null.");
        }
        delegate = target;
    }

    /**
     * Immediately delegates to the underlying proxied session.
     */
    public Serializable getId() {
        return delegate.getId();
    }

    /**
     * Immediately delegates to the underlying proxied session.
     */
    public long getStartTimestamp() {
        return delegate.getStartTimestamp();
    }

    /**
     * Immediately delegates to the underlying proxied session.
     */
    public long getLastAccessTime() {
        return delegate.getLastAccessTime();
    }

    /**
     * Immediately delegates to the underlying proxied session.
     */
    public long getTimeout() throws InvalidSessionException {
        return delegate.getTimeout();
    }

    /**
     * Immediately delegates to the underlying proxied session.
     */
    public void setTimeout(long maxIdleTimeInMillis) throws InvalidSessionException {
        delegate.setTimeout(maxIdleTimeInMillis);
    }

    /**
     * Immediately delegates to the underlying proxied session.
     */
    public String getHost() {
        return delegate.getHost();
    }

    /**
     * Immediately delegates to the underlying proxied session.
     */
    public void touch() throws InvalidSessionException {
        delegate.touch();
    }

    /**
     * Immediately delegates to the underlying proxied session.
     */
    public void stop() throws InvalidSessionException {
        delegate.stop();
    }

    /**
     * Immediately delegates to the underlying proxied session.
     */
    public Collection<Object> getAttributeKeys() throws InvalidSessionException {
        return delegate.getAttributeKeys();
    }

    /**
     * Immediately delegates to the underlying proxied session.
     */
    public Object getAttribute(Object key) throws InvalidSessionException {
        return delegate.getAttribute(key);
    }

    /**
     * Immediately delegates to the underlying proxied session.
     */
    public void setAttribute(Object key, Object value) throws InvalidSessionException {
        delegate.setAttribute(key, value);
    }

    /**
     * Immediately delegates to the underlying proxied session.
     */
    public Object removeAttribute(Object key) throws InvalidSessionException {
        return delegate.removeAttribute(key);
    }

}
