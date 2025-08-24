package top.ilovemyhome.security.soy.core.mgt.session;


import top.ilovemyhome.commons.common.text.StrUtils;
import top.ilovemyhome.commons.common.util.MapContext;

import java.io.Serializable;
import java.util.Map;

/**
 * Default implementation of the {@link SessionContext} interface which provides getters and setters that
 * wrap interaction with the underlying backing context map.
 *
 * @since 1.0
 */
public class DefaultSessionContext extends MapContext implements SessionContext {


    private static final String HOST = DefaultSessionContext.class.getName() + ".HOST";
    private static final String SESSION_ID = DefaultSessionContext.class.getName() + ".SESSION_ID";

    public DefaultSessionContext() {
        super();
    }

    public DefaultSessionContext(Map<String, Object> map) {
        super(map);
    }

    public String getHost() {
        return getTypedValue(HOST, String.class);
    }

    public void setHost(String host) {
        if (StrUtils.hasText(host)) {
            put(HOST, host);
        }
    }

    public Serializable getSessionId() {
        return getTypedValue(SESSION_ID, Serializable.class);
    }

    public void setSessionId(Serializable sessionId) {
        nullSafePut(SESSION_ID, sessionId);
    }
}
