package top.ilovemyhome.benchmark.server.application;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.typesafe.config.Config;
import io.muserver.*;
import io.muserver.openapi.OpenAPIObjectBuilder;
import io.muserver.rest.CollectionParameterStrategy;
import io.muserver.rest.RestHandlerBuilder;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.benchmark.server.web.api.BenchmarkTestCaseHandler;
import top.ilovemyhome.benchmark.server.web.api.BenchmarkTestResultHandler;
import top.ilovemyhome.benchmark.server.web.api.FooUserHandler;
import top.ilovemyhome.commons.muserver.security.AppSecurityContext;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.muserver.ContextHandlerBuilder.context;
import static io.muserver.handlers.ResourceHandlerBuilder.classpathHandler;
import static io.muserver.openapi.ComponentsObjectBuilder.componentsObject;
import static io.muserver.openapi.ExternalDocumentationObjectBuilder.externalDocumentationObject;
import static io.muserver.openapi.InfoObjectBuilder.infoObject;
import static io.muserver.openapi.SecurityRequirementObjectBuilder.securityRequirementObject;
import static io.muserver.openapi.SecuritySchemeObjectBuilder.securitySchemeObject;

public class WebServerBootstrap {

    public static MuServer start(AppContext appContext) {
        return startMuServer(appContext);
    }

    private static MuServer startMuServer(AppContext appContext) {
        Config config = appContext.getConfig();
        String contextPath = config.getString("server.contextPath");
        int port = config.getInt("server.port");
        LOGGER.info("Start mu server on port: {}.", port);
        long start = System.currentTimeMillis();
        RouteHandler rootHandler = (req, res, map) -> res.redirect("/" + contextPath + "/index.html");

        AppSecurityContext appSecurityContext = appContext.getBean("appSecurityContext", AppSecurityContext.class);
        boolean isSwaggerEnabled = config.getBoolean("server.swagger-ui.enabled");

        //The context handler for swagger-ui and rest api
        ContextHandlerBuilder contextHandlerBuilder = context(contextPath);
        contextHandlerBuilder.addHandler(Method.GET, "/", rootHandler);
        if (isSwaggerEnabled) {
            contextHandlerBuilder.addHandler(context("/swagger-ui")
                .addHandler(classpathHandler("/swagger-ui"))
            );
        }
        contextHandlerBuilder
            .addHandler(classpathHandler("/static"))
            .addHandler(createRestHandler(appContext).addRequestFilter(appSecurityContext.getFacetFilter()));

        MuServerBuilder muServerBuilder = MuServerBuilder.httpServer()
            .withHttpPort(port)
            .addResponseCompleteListener(info -> {
                MuRequest req = info.request();
                MuResponse resp = info.response();
                LOGGER.info("Response completed: success={}, remoteAddr={}, clientAddress={}, req={}, status={}, duration={}."
                    , info.completedSuccessfully(), req.remoteAddress(), req.clientIP(), req, resp.status(), info.duration());
            })
            .withIdleTimeout(30, TimeUnit.MINUTES)
            .withMaxRequestSize(300_000_000) //300MB
            .addHandler(Method.GET, "/", rootHandler)
            .addHandler(contextHandlerBuilder);

        MuServer muServer = muServerBuilder.start();

        LOGGER.info("Mu server started in {} ms.", System.currentTimeMillis() - start);
        LOGGER.info("Started app at {}.", muServer.uri().resolve("/" + contextPath));
        LOGGER.info("api.html at {}.", muServer.uri().resolve("/" + contextPath + "/api.html"));
        LOGGER.info("openapi.json at {}.", muServer.uri().resolve("/" + contextPath + "/openapi.json"));
        return muServer;
    }

    private static JacksonJsonProvider createJacksonJsonProvider() {
        ObjectMapper objectMapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // formatter
        DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        //For java 8 time
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(YearMonth.class, new YearMonthDeserializer(yearMonthFormatter));
        javaTimeModule.addSerializer(YearMonth.class, new YearMonthSerializer(yearMonthFormatter));

        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return new JacksonJsonProvider(objectMapper);
    }

    private static RestHandlerBuilder createRestHandler(AppContext appContext) {

        return RestHandlerBuilder
            .restHandler(new FooUserHandler(appContext)
                , new BenchmarkTestCaseHandler(appContext)
                , new BenchmarkTestResultHandler(appContext)
            )
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
                    .withComponents(componentsObject()
                        .withSecuritySchemes(Map.of("BasicAuth", securitySchemeObject()
                                    .withType("http")
                                    .withScheme("Basic")
                                    .build()
                                , "BearerAuth", securitySchemeObject()
                                    .withType("http")
                                    .withScheme("Bearer")
                                    .withBearerFormat("JWT")
                                    .build()
                            )
                        )
                        .build()
                    )
                    .withSecurity(List.of(
                        securityRequirementObject()
                            .withRequirements(Map.of("BasicAuth", List.of()))
                            .build()
                        , securityRequirementObject()
                            .withRequirements(Map.of("BearerAuth", List.of()))
                            .build()
                    ))
                    .withInfo(
                        infoObject()
                            .withTitle("Easy benchmark server API")
                            .withDescription("This is a API for the easy benchmark application!")
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
