package top.ilovemyhome.security.soy.core.mgt.session;

import java.io.Serializable;

/**
 * Default implementation of the {@link SessionKey} interface, which allows setting and retrieval of a concrete
 * {@link #getSessionId() sessionId} that the {@code SessionManager} implementation can use to look up a
 * {@code Session} instance.
 *
 * @since 1.0
 */
public class DefaultSessionKey implements SessionKey, Serializable {

    private Serializable sessionId;

    public DefaultSessionKey() {
    }

    public DefaultSessionKey(Serializable sessionId) {
        this.sessionId = sessionId;
    }

    public void setSessionId(Serializable sessionId) {
        this.sessionId = sessionId;
    }

    public Serializable getSessionId() {
        return this.sessionId;
    }
}
