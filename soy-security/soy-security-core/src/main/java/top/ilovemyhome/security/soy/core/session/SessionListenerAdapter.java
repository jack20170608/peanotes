package top.ilovemyhome.security.soy.core.session;

/**
 * Simple adapter implementation of the {@link SessionListener} interface, effectively providing
 * no-op implementations of all methods.
 *
 * @since 1.0
 */
public class SessionListenerAdapter implements SessionListener {

    /**
     * Adapter no-op implementation - does nothing and returns immediately.
     *
     * @param session the session that has started.
     */
    public void onStart(Session session) {
        //no-op
    }

    /**
     * Adapter no-op implementation - does nothing and returns immediately.
     *
     * @param session the session that has stopped.
     */
    public void onStop(Session session) {
        //no-op
    }

    /**
     * Adapter no-op implementation - does nothing and returns immediately.
     *
     * @param session the session that has expired.
     */
    public void onExpiration(Session session) {
        //no-op
    }
}
