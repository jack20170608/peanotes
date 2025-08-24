package top.ilovemyhome.security.soy.core.mgt.session;


import top.ilovemyhome.security.soy.core.session.Session;

import java.io.Serializable;

/**
 * Interface allowing pluggable session ID generation strategies to be used with various {@link SessionDAO}
 * implementations.
 * <h2>Usage</h2>
 * SessionIdGenerators are usually only used when ID generation is separate from creating the
 * Session record in the EIS data store.  Some EIS data stores, such as relational databases, can generate the id
 * at the same time the record is created, such as when using auto-generated primary keys.  In these cases, a
 * SessionIdGenerator does not need to be configured.
 * <p/>
 * However, if you want to customize how session IDs are created before persisting the Session record into the data
 * store, you can implement this interface and typically inject it into an {@link AbstractSessionDAO} instance.
 *
 * @see org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator JavaUuidSessionIdGenerator
 * @see org.apache.shiro.session.mgt.eis.RandomSessionIdGenerator RandomSessionIdGenerator
 * @since 1.0
 */
@FunctionalInterface
public interface SessionIdGenerator {

    /**
     * Generates a new ID to be applied to the specified {@code Session} instance.
     *
     * @param session the {@link Session} instance to which the ID will be applied.
     * @return the id to assign to the specified {@link Session} instance before adding a record to the EIS data store.
     */
    Serializable generateId(Session session);

}
