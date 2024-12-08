package top.ilovemyhome.peanotes.backend.common.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public final class HttpUtils {

    public static HttpResponse<String> post(URI uri, int timeoutSeconds, Map<String, String> headers, String body) {
        requireNonNull(headers);
        try (HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .sslContext(TrustAllSslContext.getInstance())
            .connectTimeout(Duration.ofSeconds(5))
            .build()) {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri);
            headers.forEach(requestBuilder::header);
            return httpClient.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
                .get(timeoutSeconds, TimeUnit.SECONDS);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static final String XXL_JOB_ACCESS_TOKEN = "XXL-JOB-ACCESS-TOKEN";

}
