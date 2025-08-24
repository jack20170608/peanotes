package top.ilovemyhome.security.soy.core.mgt.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.commons.common.util.CollectionUtil;
import top.ilovemyhome.security.soy.core.event.EventBus;
import top.ilovemyhome.security.soy.core.event.EventBusAware;
import top.ilovemyhome.security.soy.core.exception.authz.AuthorizationException;
import top.ilovemyhome.security.soy.core.exception.session.InvalidSessionException;
import top.ilovemyhome.security.soy.core.exception.session.SessionException;
import top.ilovemyhome.security.soy.core.exception.session.UnknownSessionException;
import top.ilovemyhome.security.soy.core.session.Session;
import top.ilovemyhome.security.soy.core.session.SessionListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Abstract implementation supporting the {@link NativeSessionManager NativeSessionManager} interface, supporting
 * {@link SessionListener SessionListener}s and application of the
 * {@link #getGlobalSessionTimeout() globalSessionTimeout}.
 *
 * @since 1.0
 */
@SuppressWarnings({"checkstyle:MethodCount"})
public abstract class AbstractNativeSessionManager implements NativeSessionManager, EventBusAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNativeSessionManager.class);

    protected static final long MILLIS_PER_SECOND = 1000;
    protected static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    protected static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

    public static final long DEFAULT_GLOBAL_SESSION_TIMEOUT = 60 * MILLIS_PER_MINUTE;

    private long globalSessionTimeout = DEFAULT_GLOBAL_SESSION_TIMEOUT;

    private EventBus eventBus;

    private Collection<SessionListener> listeners;

    public AbstractNativeSessionManager() {
        this.listeners = new ArrayList<SessionListener>();
    }

    public void setSessionListeners(Collection<SessionListener> listeners) {
        this.listeners = listeners != null ? listeners : new ArrayList<SessionListener>();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public Collection<SessionListener> getSessionListeners() {
        return this.listeners;
    }

    public long getGlobalSessionTimeout() {
        return globalSessionTimeout;
    }

    public void setGlobalSessionTimeout(long globalSessionTimeout) {
        this.globalSessionTimeout = globalSessionTimeout;
    }

    /**
     * Returns the EventBus used to publish SessionEvents.
     *
     * @return the EventBus used to publish SessionEvents.
     * @since 1.3
     */
    protected EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Sets the EventBus to use to publish SessionEvents.
     *
     * @param eventBus the EventBus to use to publish SessionEvents.
     * @since 1.3
     */
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Publishes events on the event bus if the event bus is non-null, otherwise does nothing.
     *
     * @param event the event to publish on the event bus if the event bus exists.
     * @since 1.3
     */
    protected void publishEvent(Object event) {
        if (this.eventBus != null) {
            this.eventBus.publish(event);
        }
    }

    public Session start(SessionContext context) {
        Session session = createSession(context);
        applyGlobalSessionTimeout(session);
        onStart(session, context);
        notifyStart(session);
        //Don't expose the EIS-tier Session object to the client-tier:
        return createExposedSession(session, context);
    }

    /**
     * Creates a new {@code Session Session} instance based on the specified (possibly {@code null})
     * initialization data.  Implementing classes must manage the persistent state of the returned session such that it
     * could later be acquired via the {@link #getSession(SessionKey)} method.
     *
     * @param context the initialization data that can be used by the implementation or underlying
     *                {@link SessionFactory} when instantiating the internal {@code Session} instance.
     * @return the new {@code Session} instance.
     * @throws top.ilovemyhome.security.soy.core.exception.authz.HostUnauthorizedException if the system access control policy restricts access based
     *                                                          on client location/IP and
     *                                                          the specified hostAddress hasn't been enabled.
     * @throws AuthorizationException                           if the system access control policy does not allow
     *                                                          the currently executing caller to start sessions.
     */
    protected abstract Session createSession(SessionContext context) throws AuthorizationException;

    protected void applyGlobalSessionTimeout(Session session) {
        session.setTimeout(getGlobalSessionTimeout());
        onChange(session);
    }

    /**
     * Template method that allows subclasses to react to a new session being created.
     * <p/>
     * This method is invoked <em>before</em> any session listeners are notified.
     *
     * @param session the session that was just {@link #createSession created}.
     * @param context the {@link SessionContext SessionContext} that was used to start the session.
     */
    protected void onStart(Session session, SessionContext context) {
    }

    public Session getSession(SessionKey key) throws SessionException {
        Session session = lookupSession(key);
        return session != null ? createExposedSession(session, key) : null;
    }

    private Session lookupSession(SessionKey key) throws SessionException {
        if (key == null) {
            throw new NullPointerException("SessionKey argument cannot be null.");
        }
        return doGetSession(key);
    }

    private Session lookupRequiredSession(SessionKey key) throws SessionException {
        Session session = lookupSession(key);
        if (session == null) {
            String msg = "Unable to locate required Session instance based on SessionKey [" + key + "].";
            throw new UnknownSessionException(msg);
        }
        return session;
    }

    protected abstract Session doGetSession(SessionKey key) throws InvalidSessionException;

    protected Session createExposedSession(Session session, SessionContext context) {
        return new DelegatingSession(this, new DefaultSessionKey(session.getId()));
    }

    protected Session createExposedSession(Session session, SessionKey key) {
        return new DelegatingSession(this, new DefaultSessionKey(session.getId()));
    }

    /**
     * Returns the session instance to use to pass to registered {@code SessionListener}s for notification
     * that the session has been invalidated (stopped or expired).
     * <p/>
     * The default implementation returns an {@link ImmutableProxiedSession ImmutableProxiedSession} instance to ensure
     * that the specified {@code session} argument is not modified by any listeners.
     *
     * @param session the {@code Session} object being invalidated.
     * @return the {@code Session} instance to use to pass to registered {@code SessionListener}s for notification.
     */
    protected Session beforeInvalidNotification(Session session) {
        return new ImmutableProxiedSession(session);
    }

    /**
     * Notifies any interested {@link SessionListener}s that a Session has started.  This method is invoked
     * <em>after</em> the {@link #onStart onStart} method is called.
     *
     * @param session the session that has just started that will be delivered to any
     *                {@link #setSessionListeners(Collection) registered} session listeners.
     * @see SessionListener#onStart(org.apache.shiro.session.Session)
     */
    protected void notifyStart(Session session) {
        for (SessionListener listener : this.listeners) {
            listener.onStart(session);
        }
    }

    protected void notifyStop(Session session) {
        Session forNotification = beforeInvalidNotification(session);
        for (SessionListener listener : this.listeners) {
            listener.onStop(forNotification);
        }
    }

    protected void notifyExpiration(Session session) {
        Session forNotification = beforeInvalidNotification(session);
        for (SessionListener listener : this.listeners) {
            listener.onExpiration(forNotification);
        }
    }

    @Override
    public long getStartTimestamp(SessionKey key) {
        return lookupRequiredSession(key).getStartTimestamp();
    }

    @Override
    public long getLastAccessTime(SessionKey key) {
        return lookupRequiredSession(key).getLastAccessTime();
    }

    public long getTimeout(SessionKey key) throws InvalidSessionException {
        return lookupRequiredSession(key).getTimeout();
    }

    public void setTimeout(SessionKey key, long maxIdleTimeInMillis) throws InvalidSessionException {
        Session s = lookupRequiredSession(key);
        s.setTimeout(maxIdleTimeInMillis);
        onChange(s);
    }

    public void touch(SessionKey key) throws InvalidSessionException {
        Session s = lookupRequiredSession(key);
        s.touch();
        onChange(s);
    }

    public String getHost(SessionKey key) {
        return lookupRequiredSession(key).getHost();
    }

    public Collection<Object> getAttributeKeys(SessionKey key) {
        Collection<Object> c = lookupRequiredSession(key).getAttributeKeys();
        if (!CollectionUtil.isEmpty(c)) {
            return Collections.unmodifiableCollection(c);
        }
        return Collections.emptySet();
    }

    public Object getAttribute(SessionKey sessionKey, Object attributeKey) throws InvalidSessionException {
        return lookupRequiredSession(sessionKey).getAttribute(attributeKey);
    }

    public void setAttribute(SessionKey sessionKey, Object attributeKey, Object value) throws InvalidSessionException {
        if (value == null) {
            removeAttribute(sessionKey, attributeKey);
        } else {
            Session s = lookupRequiredSession(sessionKey);
            s.setAttribute(attributeKey, value);
            onChange(s);
        }
    }

    public Object removeAttribute(SessionKey sessionKey, Object attributeKey) throws InvalidSessionException {
        Session s = lookupRequiredSession(sessionKey);
        Object removed = s.removeAttribute(attributeKey);
        if (removed != null) {
            onChange(s);
        }
        return removed;
    }

    public boolean isValid(SessionKey key) {
        try {
            checkValid(key);
            return true;
        } catch (InvalidSessionException e) {
            return false;
        }
    }

    public void stop(SessionKey key) throws InvalidSessionException {
        Session session = lookupRequiredSession(key);
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stopping session with id [" + session.getId() + "]");
            }
            session.stop();
            onStop(session, key);
            notifyStop(session);
        } finally {
            afterStopped(session);
        }
    }

    protected void onStop(Session session, SessionKey key) {
        onStop(session);
    }

    protected void onStop(Session session) {
        onChange(session);
    }

    protected void afterStopped(Session session) {
    }

    public void checkValid(SessionKey key) throws InvalidSessionException {
        //just try to acquire it.  If there is a problem, an exception will be thrown:
        lookupRequiredSession(key);
    }

    protected void onChange(Session s) {
    }
}
