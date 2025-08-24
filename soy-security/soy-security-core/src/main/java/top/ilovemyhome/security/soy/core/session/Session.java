package top.ilovemyhome.security.soy.core.session;

import top.ilovemyhome.security.soy.core.exception.session.InvalidSessionException;

import java.io.Serializable;
import java.util.Collection;


public interface Session {


    Serializable getId();

    long getStartTimestamp();

    long getLastAccessTime();

    long getTimeout() throws InvalidSessionException;


    void setTimeout(long maxIdleTimeInMillis) throws InvalidSessionException;


    String getHost();


    void touch() throws InvalidSessionException;


    void stop() throws InvalidSessionException;


    Collection<Object> getAttributeKeys() throws InvalidSessionException;


    Object getAttribute(Object key) throws InvalidSessionException;


    void setAttribute(Object key, Object value) throws InvalidSessionException;


    Object removeAttribute(Object key) throws InvalidSessionException;
}
