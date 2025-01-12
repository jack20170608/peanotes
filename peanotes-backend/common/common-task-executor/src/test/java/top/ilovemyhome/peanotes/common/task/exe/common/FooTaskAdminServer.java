package top.ilovemyhome.peanotes.common.task.exe.common;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import io.muserver.Method;
import io.muserver.MuServer;
import io.muserver.openapi.OpenAPIObjectBuilder;
import io.muserver.rest.CollectionParameterStrategy;
import io.muserver.rest.RestHandlerBuilder;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.exe.web.TaskResource;


import java.net.URI;
import java.util.Objects;

import static io.muserver.MuServerBuilder.muServer;
import static io.muserver.openapi.ExternalDocumentationObjectBuilder.externalDocumentationObject;
import static io.muserver.openapi.InfoObjectBuilder.infoObject;

public class FooTaskAdminServer {

    //used for local testing
    public static void main(String[] args) throws Exception {
        FooTaskAdminServer server = new FooTaskAdminServer();
        LOGGER.info("Admin server started at uri:[{}].", server.muServer.httpUri());
    }

    public FooTaskAdminServer(){
        this(0);
    }
    public FooTaskAdminServer(int port) {
        this.port = port;
        MuServer muServer = muServer()
            .withHttpPort(port)
            .addHandler(Method.GET, "/hello", (req, res, map) -> {
                req.headers().entries().forEach(entry -> {
                    res.headers().add(entry.getKey(), entry.getValue());
                });
                res.write("Hello World");
            }).addHandler(createRestHandler())
            .start();
        this.muServer = muServer;
        LOGGER.info("Mu Server started at {}", muServer.httpUri());
        LOGGER.info("Api url at {}", muServer.httpUri().resolve("/api.html"));
    }

    private RestHandlerBuilder createRestHandler() {
        return RestHandlerBuilder
            .restHandler(taskAdminController)
            .addCustomReader(new JacksonJsonProvider())
            .addCustomWriter(new JacksonJsonProvider())
            .withCollectionParameterStrategy(CollectionParameterStrategy.NO_TRANSFORM)
            .withOpenApiHtmlUrl("/api.html")
            .withOpenApiJsonUrl("/openapi.json")
            .withOpenApiDocument(
                OpenAPIObjectBuilder.openAPIObject()
                    .withInfo(
                        infoObject()
                            .withTitle("User API document")
                            .withDescription("This is a foo task admin server.")
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

    public MuServer getMuServer() {
        return muServer;
    }

    public TaskAdminController getTaskAdminController() {
        return taskAdminController;
    }

    public void stop() {
        if (Objects.nonNull(muServer)) {
            muServer.stop();
        }
    }

    private final int port;

    private final MuServer muServer;

    public final TaskAdminController taskAdminController = new TaskAdminController();

    private static final Logger LOGGER = LoggerFactory.getLogger(FooTaskAdminServer.class);
}
