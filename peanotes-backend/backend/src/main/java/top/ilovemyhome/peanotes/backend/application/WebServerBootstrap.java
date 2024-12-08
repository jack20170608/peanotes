package top.ilovemyhome.peanotes.backend.application;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.hsbc.cranker.connector.*;
import com.typesafe.config.Config;
import io.muserver.*;
import io.muserver.handlers.ResourceHandlerBuilder;
import io.muserver.openapi.OpenAPIObjectBuilder;
import io.muserver.rest.CollectionParameterStrategy;
import io.muserver.rest.RestHandlerBuilder;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.web.handlers.HealthHandler;
import top.ilovemyhome.peanotes.backend.web.handlers.OperationHandler;
import top.ilovemyhome.peanotes.backend.web.handlers.system.SystemHandler;
import top.ilovemyhome.peanotes.backend.web.handlers.system.TaskHandler;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hsbc.cranker.connector.CrankerConnectorBuilder.CRANKER_PROTOCOL_3;
import static io.muserver.ContextHandlerBuilder.context;
import static io.muserver.openapi.ExternalDocumentationObjectBuilder.externalDocumentationObject;
import static io.muserver.openapi.InfoObjectBuilder.infoObject;

public class WebServerBootstrap {

    public static void start(AppContext appContext) {
        MuServer targetServer = startMuServer(appContext);
        CrankerConnector connector = startCrankerConnector(appContext, targetServer);

        AtomicInteger exceptionCounter = new AtomicInteger(0);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            for (RouterRegistration router : connector.routers()) {
                LOGGER.info("Router expects {} and is {}, errorCount={}", router.expectedWindowSize(), router.idleSocketSize(), exceptionCounter.get());
            }
        }, 0, 1, TimeUnit.MINUTES);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down....");
            executorService.shutdown();
            try {
                connector.stop(9, TimeUnit.SECONDS);
            } catch (Exception e) {
                LOGGER.info("Error stopping connector", e);
            }
            targetServer.stop();
            LOGGER.info("Shutdown complete");
        }));
    }

    private static CrankerConnector startCrankerConnector(AppContext appContext, MuServer targetServer) {
        String crankerServerUrl = appContext.getConfig().getString("cranker-gateway.url");
        LOGGER.info("Cranker Server Url is: {}.", crankerServerUrl);
        String contextPath = appContext.getConfig().getString("server.context-path");

        return CrankerConnectorBuilder.connector()
            .withPreferredProtocols(List.of(CRANKER_PROTOCOL_3))
//            .withDomain("*")
            .withRouterUris(() -> List.of(URI.create(crankerServerUrl)))
            .withHttpClient(CrankerConnectorBuilder.createHttpClient(true).build())
            .withComponentName(appContext.getApplicationName())
            .withRoute(contextPath)
            .withTarget(targetServer.uri())
            .withRouterRegistrationListener(new RouterEventListener() {
                public void onRegistrationChanged(ChangeData data) {
                    LOGGER.info("Router registration changed: " + data);
                }

                public void onSocketConnectionError(RouterRegistration router1, Throwable exception) {
                    LOGGER.warn("Error connecting to " + router1, exception);
                }
            })
            .withProxyEventListener(new ProxyEventListener() {
                @Override
                public void onProxyError(HttpRequest request, Throwable error) {
                    LOGGER.warn("onProxyError, request=" + request, error);
                }
            })
            .start();
    }

    private static MuServer startMuServer(AppContext appContext) {
        Config config = appContext.getConfig();
        String contextPath = config.getString("server.context-path");
        int port = config.getInt("server.port");
        LOGGER.info("Start mu server on port: {}.", port);
        long start = System.currentTimeMillis();
        MuServerBuilder muServerBuilder = MuServerBuilder.httpServer()
            .withHttpPort(port)
            .addResponseCompleteListener(info -> {
                MuRequest req = info.request();
                MuResponse resp = info.response();
                LOGGER.info("Response completed: success={}, remoteAddr={}, clientAddress={}, req={}, status={}, duration={}."
                    , info.completedSuccessfully(), req.remoteAddress(), req.clientIP(), req, resp.status(), info.duration());
            })
            .withIdleTimeout(10, TimeUnit.MINUTES)
            .withMaxRequestSize(300_000_000) //300MB
            .addHandler(Method.GET, "/health", new HealthHandler())
            .addHandler(context(contextPath)
                .addHandler(ResourceHandlerBuilder.classpathHandler("/web/swagger-ui"))
                .addHandler(createRestHandler(appContext))

            );

        MuServer muServer = muServerBuilder.start();
        LOGGER.info("Mu server started in {} ms.", System.currentTimeMillis() - start);
        LOGGER.info("Started app at {}.", muServer.uri().resolve("/" + contextPath));
        return muServer;
    }

    private static RestHandlerBuilder createRestHandler(AppContext appContext) {
        OperationHandler operationHandler = new OperationHandler(appContext);
        SystemHandler systemHandler = new SystemHandler(appContext);
        TaskHandler taskHandler = new TaskHandler(appContext);

        return RestHandlerBuilder
            .restHandler(operationHandler, systemHandler, taskHandler)
            .addCustomReader(new JacksonJsonProvider())
            .addCustomWriter(new JacksonJsonProvider())
            .withCollectionParameterStrategy(CollectionParameterStrategy.NO_TRANSFORM)
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
                            .withTitle("User API document")
                            .withDescription("This is a sample API for the graphql sample")
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

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServerBootstrap.class);
}
