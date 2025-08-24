package top.ilovemyhome.security.soy.core.mgt.session;


import top.ilovemyhome.security.soy.core.session.Session;

import java.io.Serializable;
import java.util.UUID;

/**
 * {@link SessionIdGenerator} that generates String values of JDK {@link UUID}'s as the session IDs.
 *
 * @since 1.0
 */
public class JavaUuidSessionIdGenerator implements SessionIdGenerator {

    /**
     * Ignores the method argument and simply returns
     * {@code UUID}.{@link UUID#randomUUID() randomUUID()}.{@code toString()}.
     *
     * @param session the {@link Session} instance to which the ID will be applied.
     * @return the String value of the JDK's next {@link UUID#randomUUID() randomUUID()}.
     */
    public Serializable generateId(Session session) {
        return UUID.randomUUID().toString();
    }
}
