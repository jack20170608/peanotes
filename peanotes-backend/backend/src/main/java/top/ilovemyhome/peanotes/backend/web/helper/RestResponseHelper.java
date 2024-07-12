package top.ilovemyhome.peanotes.backend.web.helper;

import jakarta.ws.rs.core.Response;

public final class RestResponseHelper {

    public static <T> RestResponseWrapper<T> ok(final T data) {
        return new RestResponseWrapper<>(Response.Status.OK.getStatusCode(),
            "OK", data);
    }

    private RestResponseHelper() {}
}
