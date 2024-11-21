package top.ilovemyhome.peanotes.common.task.admin.core.http;

import top.ilovemyhome.peanotes.common.task.admin.core.model.ReturnT;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;
import static top.ilovemyhome.peanotes.common.task.admin.core.model.ReturnT.FAIL_CODE;

public final class HttpUtils {

    public static ReturnT<String> post(String uri, int timeoutSeconds, Map<String, String> headers, String body) {
        requireNonNull(headers);
        try (HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .sslContext(TrustAllSslContext.getInstance())
            .connectTimeout(Duration.ofSeconds(5))
            .build()) {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(uri));
            headers.forEach(requestBuilder::header);
            HttpResponse<String> httpResponse = httpClient.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
                .get(timeoutSeconds, TimeUnit.SECONDS);
            return new ReturnT<>(httpResponse.statusCode(), httpResponse.body());
        }catch (Exception e) {
            return new ReturnT<>(FAIL_CODE, e.getMessage());
        }
    }


    public static final String XXL_JOB_ACCESS_TOKEN = "XXL-JOB-ACCESS-TOKEN";



}
