package top.ilovemyhome.commons.muserver;

import io.muserver.Cookie;
import io.muserver.Headers;
import io.muserver.Method;
import io.muserver.MuRequest;
import io.muserver.RequestParameters;
import org.mockito.Mockito;
import top.ilovemyhome.commons.common.codec.DigestUtils;
import top.ilovemyhome.commons.muserver.security.core.User;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class SharedTestResources {

    private SharedTestResources() {
    }

    public static List<User> createTestingUsers() {
        return List.of(
            new User("1", "admin", "Admin", Set.of("admin"), DigestUtils.sha256Hex("1"), Map.of("band", "3"))
            , new User("2", "ro", "ReadOnly", Set.of("ro"), DigestUtils.sha256Hex("2"), Map.of("band", "5"))
            , new User("3", "rw", "ReadWrite", Set.of("rw"), DigestUtils.sha256Hex("3"), Map.of("band", "4"))
            , new User("4", "foo", "FooUser", Set.of("ro", "foo"), DigestUtils.sha256Hex("4"), Map.of("band", "5"))
        );
    }

    /**
     * 创建标准的测试 Headers
     *
     * @return 预配置的 Headers 映射
     */
    public static Map<String, String> createStandardHeaders() {
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("User-Agent", "Mozilla/5.0");
        headersMap.put("Accept", "application/json");
        headersMap.put("Accept-Encoding", "gzip, deflate");
        headersMap.put("Accept-Language", "en-US,en;q=0.9");
        headersMap.put("Cache-Control", "no-cache");
        headersMap.put("Connection", "keep-alive");
        headersMap.put("Content-Type", "application/json");
        return headersMap;
    }

    /**
     * 创建标准的查询参数
     *
     * @param mockParameters mock 的 RequestParameters 对象
     */
    public static void configureStandardQueryParameters(RequestParameters mockParameters, Map<String, String> queryParametersMap) {
        queryParametersMap.forEach((key, value) ->
            Mockito.when(mockParameters.get(key)).thenReturn(value));
    }

    /**
     * 配置 Mock Headers 对象
     *
     * @param mockHeaders mock 的 Headers 对象
     * @param headersMap  要设置的 headers 数据
     */
    public static void configureMockHeaders(Headers mockHeaders, Map<String, String> headersMap) {
        headersMap.forEach((key, value) ->
            Mockito.when(mockHeaders.get(key)).thenReturn(value));
        Mockito.when(mockHeaders.iterator()).thenReturn(headersMap.entrySet().iterator());
    }


    /**
     * 创建标准的测试 Cookies
     *
     * @return Cookie 列表
     */
    public static List<Cookie> configureMockCookies(Map<String, String> cookiesMap) {
        return cookiesMap.entrySet().stream().map(entry -> Cookie.builder()
            .withName(entry.getKey())
            .withValue(entry.getValue())
            .build()).collect(Collectors.toList());
    }

    /**
     * 配置标准的 Mock Request
     *
     * @param mockRequest mock 的 MuRequest 对象
     */
    public static void configureStandardMockRequest(MuRequest mockRequest
        , Map<String, String> headersMap
        , Map<String, String> queryParametersMap
        , Map<String, String> cookieMap) {
        //The request is always HTTP/1.1
        Mockito.when(mockRequest.protocol()).thenReturn("HTTP/1.1");
        Mockito.when(mockRequest.method()).thenReturn(Method.POST);
        Mockito.when(mockRequest.uri()).thenReturn(URI.create("http://localhost:8080/api/test"));
        Mockito.when(mockRequest.contextPath()).thenReturn("/api");
        Mockito.when(mockRequest.clientIP()).thenReturn("192.168.1.100");
        Mockito.when(mockRequest.remoteAddress()).thenReturn("192.168.1.100");
        Mockito.when(mockRequest.contentType()).thenReturn("application/json");

        //The headers
        Headers mockHeaders = Mockito.mock(Headers.class);
        configureMockHeaders(mockHeaders, headersMap);
        Mockito.when(mockRequest.headers()).thenReturn(mockHeaders);

        //The Query
        RequestParameters requestParameters = Mockito.mock(RequestParameters.class);
        configureStandardQueryParameters(requestParameters, queryParametersMap);
        Mockito.when(mockRequest.query()).thenReturn(requestParameters);

        //The Cookies
        List<Cookie> mockCookies = configureMockCookies(cookieMap);
        Mockito.when(mockRequest.cookies()).thenReturn(mockCookies);
    }


}
