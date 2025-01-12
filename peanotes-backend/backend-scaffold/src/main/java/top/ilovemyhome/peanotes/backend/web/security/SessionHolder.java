package top.ilovemyhome.peanotes.backend.web.security;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.time.Duration;

public final class SessionHolder {

    private static final Cache<String, UserAuthInfo> sessionMap = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.ofHours(2))
        .initialCapacity(100)
        .maximumSize(5000)
        .build();

    public static void set(String key, UserAuthInfo info) {
        sessionMap.put(key, info);
    }

    public static UserAuthInfo get(String key) {
        return sessionMap.getIfPresent(key);
    }

}
