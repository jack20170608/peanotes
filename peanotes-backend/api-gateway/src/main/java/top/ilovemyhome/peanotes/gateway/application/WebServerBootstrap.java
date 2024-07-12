package top.ilovemyhome.peanotes.gateway.application;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.hsbc.cranker.mucranker.*;
import com.typesafe.config.Config;
import io.muserver.*;
import io.muserver.handlers.ResourceHandlerBuilder;
import io.muserver.openapi.OpenAPIObjectBuilder;
import io.muserver.rest.RestHandlerBuilder;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.gateway.web.handlers.FooHandler;
import top.ilovemyhome.peanotes.gateway.web.handlers.HealthHandler;
import top.ilovemyhome.peanotes.gateway.web.handlers.LoginHandler;
import top.ilovemyhome.peanotes.gateway.web.handlers.StatsHandler;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static io.muserver.ContextHandlerBuilder.context;
import static io.muserver.MuServerBuilder.muServer;
import static io.muserver.openapi.ExternalDocumentationObjectBuilder.externalDocumentationObject;
import static io.muserver.openapi.InfoObjectBuilder.infoObject;

public class WebServerBootstrap {


    public static void start(AppContext appContext){
        CrankerRouter router = startCrankerRouter(appContext);
        MuServer registrationServer = startRegistrationServer(appContext, router);
        MuServer httpServer = startMuServer(appContext, router);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            httpServer.stop();
            registrationServer.stop();
            router.stop();
        }));
    }

    private static MuServer startRegistrationServer(AppContext appContext, CrankerRouter router){
        int port = appContext.getConfig().getInt("registration.port");
        //todo by jack, will use https instead
        return muServer()
            .withHttpPort(port)
            .addHandler(Method.GET, "/health/stats", new StatsHandler(router))
            .addHandler(Method.GET, "/health/connections", (request, response, pathParams) -> {
                response.contentType("text/plain;charset=utf-8");
                for (HttpConnection con : request.server().activeConnections()) {
                    response.sendChunk(con.httpsProtocol() + " " + con.remoteAddress() + "\n");
                    for (MuRequest activeRequest : con.activeRequests()) {
                        response.sendChunk("   " + activeRequest + "\n");
                    }
                    response.sendChunk("\n");
                }
                response.sendChunk("-------");
            })
            .addHandler(Method.GET, "/health/connectors", (request, response, pathParams) -> {
                response.contentType(ContentTypes.APPLICATION_JSON);
                response.write(new JSONObject()
                    .put("services", router.collectInfo().toMap())
                    .toString(2));
            })
            .addHandler(router.createRegistrationHandler())
            .start();
    }

    private static MuServer startMuServer(AppContext appContext,CrankerRouter router) {
        Config config = appContext.getConfig();
        String contextPath = config.getString("server.context-path");
        int port = config.getInt("server.port");
        LOGGER.info("Start mu server on port: {}.", port);
        long start = System.currentTimeMillis();
        MuServerBuilder muServerBuilder = null;
        try {
            muServerBuilder = MuServerBuilder.httpServer()
                .withHttpPort(port)
                .withHttp2Config(Http2ConfigBuilder.http2EnabledIfAvailable())
                .addResponseCompleteListener(info -> {
                    MuRequest req = info.request();
                    MuResponse resp = info.response();
                    LOGGER.info("Response completed: success={}, remoteAddr={}, clientAddress={}, req={}, status={}, duration={}."
                        , info.completedSuccessfully(), req.remoteAddress(), req.clientIP(), req, resp.status(), info.duration());
                })
                .addHandler(FavIconHandler.fromClassPath("/favicon.ico"))
                .withIdleTimeout(10, TimeUnit.MINUTES)
                .withMaxRequestSize(300_000_000) //300MB
                .addHandler(Method.GET, "/health", new HealthHandler())
                .addHandler(context(contextPath)
                    .addHandler(ResourceHandlerBuilder.classpathHandler("/web/swagger-ui"))
                    .addHandler(createRestHandler(appContext))
                )
                .addHandler(router.createHttpHandler());
        } catch (IOException e) {
            LOGGER.info("Web server start failure.", e);
            throw new RuntimeException(e);
        }

        MuServer muServer = muServerBuilder.start();
        LOGGER.info("Mu server started in {} ms.", System.currentTimeMillis() - start);
        LOGGER.info("Started app at {}.", muServer.uri().resolve("/" + contextPath));
        return muServer;
    }



    private static CrankerRouter startCrankerRouter(AppContext appContext) {
        return CrankerRouterBuilder.crankerRouter()
            .withIdleTimeout(30, TimeUnit.SECONDS)
            .withPingSentAfterNoWritesFor(5, TimeUnit.SECONDS)
            .withConnectorMaxWaitInMillis(1000)
            .withProxyListeners(List.of(loggerPublisher))
            .withSupportedCrankerProtocols(List.of("cranker_3.0"))
            .start();
    }

    private static RestHandlerBuilder createRestHandler(AppContext appContext) {
        return RestHandlerBuilder
            .restHandler(new FooHandler(), new LoginHandler())
            .addCustomReader(new JacksonJsonProvider())
            .addCustomWriter(new JacksonJsonProvider())
            .withOpenApiHtmlUrl("/api.html")
            .withOpenApiJsonUrl("/openapi.json")
            .addExceptionMapper(ClientErrorException.class, e -> Response.status(Response.Status.BAD_REQUEST.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("message", e.getMessage()))
                .build())
            .addExceptionMapper(InternalServerErrorException.class, e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("message", e.getMessage()))
                .build())
            .addExceptionMapper(JsonMappingException.class, e -> Response.status(Response.Status.BAD_REQUEST.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("message", e.getMessage()))
                .build())
            .withOpenApiDocument(
                OpenAPIObjectBuilder.openAPIObject()
                    .withInfo(
                        infoObject()
                            .withTitle("Peanotes API gateway")
                            .withDescription("This is the Peanotes API gateway!!")
                            .withVersion("1.0")
                            .build())
                    .withExternalDocs(
                        externalDocumentationObject()
                            .withDescription("Documentation docs")
                            .withUrl(URI.create("https//muserver.io/jaxrs"))
                            .build()
                    )
            );
    }

    private static final ProxyListener loggerPublisher = new ProxyListener() {
        @Override
        public void onBeforeProxyToTarget(ProxyInfo info, Headers requestHeadersToTarget) throws WebApplicationException {
            final long elapsedTimeBeforeProxy = System.currentTimeMillis() - info.request().startTime();
            info.request().attributes().putIfAbsent(ELAPSED_TIME_BEFORE_PROXY, elapsedTimeBeforeProxy);
            LOGGER.info("Proxying {} from {} to {}, elapsedTimeBeforeProxy={}.", info.request(), info.request().connection().remoteAddress(), info.serviceAddress(), elapsedTimeBeforeProxy);
        }

        @Override
        public void onComplete(ProxyInfo info) {
            final Object elapsedTimeBeforeProxy = info.request().attributes().get(ELAPSED_TIME_BEFORE_PROXY);
            String elapsedTime = elapsedTimeBeforeProxy != null ? "elapsedTimeBeforeProxy=" + elapsedTimeBeforeProxy : "";
            LOGGER.info("Completed {} from {} to {}, duration={}ms, received={}, sent={}, status={}, error={}, isSSE={}, {}"
                , info.request(), info.request().connection().remoteAddress(), info.serviceAddress()
                , info.durationMillis(), info.bytesReceived(), info.bytesSent(), getStatus(info.response())
                , info.errorIfAny() == null ? null : info.errorIfAny().toString(), isSSE(info.response())
                , elapsedTime);
            if (Objects.nonNull(info.errorIfAny())) {
                LOGGER.warn("Error details", info.errorIfAny());
            }

        }
    };

    private static int getStatus(MuResponse response) {
        return response == null ? 0 : response.status();
    }

    private static boolean isSSE(MuResponse response) {
        return response != null && "text/event-stream".equals(response.headers().get(HeaderNames.CONTENT_TYPE));
    }

    private static final String ELAPSED_TIME_BEFORE_PROXY = "elapsedTimeBeforeProxy";

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServerBootstrap.class);
}
