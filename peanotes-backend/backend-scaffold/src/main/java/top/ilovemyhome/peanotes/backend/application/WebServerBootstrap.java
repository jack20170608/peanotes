package top.ilovemyhome.peanotes.backend.application;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
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
import top.ilovemyhome.peanotes.backend.interfaces.api.OperationHandler;
import top.ilovemyhome.peanotes.backend.interfaces.api.QueryHandler;
import top.ilovemyhome.peanotes.backend.web.LoginHandler;
import top.ilovemyhome.peanotes.backend.web.security.SecurityHandler;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.muserver.ContextHandlerBuilder.context;
import static io.muserver.openapi.ExternalDocumentationObjectBuilder.externalDocumentationObject;
import static io.muserver.openapi.InfoObjectBuilder.infoObject;

public class WebServerBootstrap {

    public static void start(AppContext appContext) {
        MuServer targetServer = startMuServer(appContext);
    }


    private static MuServer startMuServer(AppContext appContext) {
        Config config = appContext.getConfig();
        String contextPath = config.getString("server.contextPath");
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
            .addHandler(Method.GET, "/", (req, res, map) -> res.redirect("/" + contextPath + "/index.html"))
            .addHandler(Method.POST, "/login", new LoginHandler(appContext))
            .addHandler(context(contextPath)
                .addHandler(ResourceHandlerBuilder.classpathHandler("/static/public"))
                .addHandler(new SecurityHandler(appContext))
                .addHandler(ResourceHandlerBuilder.classpathHandler("/static/restricted"))
                .addHandler(context("api/v1")
                    .addHandler(createRestHandler(appContext)))
            );

        MuServer muServer = muServerBuilder.start();
        LOGGER.info("Mu server started in {} ms.", System.currentTimeMillis() - start);
        LOGGER.info("Started app at {}.", muServer.uri().resolve("/" + contextPath));
        return muServer;
    }

    private static JacksonJsonProvider createJacksonJsonProvider() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // formatter
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // deserializers
//        builder.deserializers(new LocalDateDeserializer(dateFormatter));
//        builder.deserializers(new LocalDateTimeDeserializer(dateTimeFormatter));

        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        return new JacksonJsonProvider(objectMapper);
    }

    private static RestHandlerBuilder createRestHandler(AppContext appContext) {

        return RestHandlerBuilder
            .restHandler(new OperationHandler(appContext), new QueryHandler(appContext))
            .addCustomReader(createJacksonJsonProvider())
            .addCustomWriter(createJacksonJsonProvider())
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
