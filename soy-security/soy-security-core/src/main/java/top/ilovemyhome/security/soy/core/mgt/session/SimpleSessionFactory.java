package top.ilovemyhome.security.soy.core.mgt.session;


import top.ilovemyhome.security.soy.core.session.Session;

/**
 * {@code SessionFactory} implementation that generates {@link SimpleSession} instances.
 *
 * @since 1.0
 */
public class SimpleSessionFactory implements SessionFactory {

    /**
     * Creates a new {@link SimpleSession SimpleSession} instance retaining the context's
     * {@link SessionContext#getHost() host} if one can be found.
     *
     * @param initData the initialization data to be used during {@link Session} creation.
     * @return a new {@link SimpleSession SimpleSession} instance
     */
    public Session createSession(SessionContext initData) {
        if (initData != null) {
            String host = initData.getHost();
            if (host != null) {
                return new SimpleSession(host);
            }
        }
        return new SimpleSession();
    }
}
