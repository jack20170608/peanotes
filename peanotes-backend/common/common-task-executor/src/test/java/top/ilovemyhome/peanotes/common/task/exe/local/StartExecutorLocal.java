package top.ilovemyhome.peanotes.common.task.exe.local;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
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
import top.ilovemyhome.peanotes.common.task.exe.TaskExecutor;
import top.ilovemyhome.peanotes.common.task.exe.TaskExecutorContext;
import top.ilovemyhome.peanotes.common.task.exe.tasks.SimpleTask;
import top.ilovemyhome.peanotes.common.task.exe.web.HealthResource;
import top.ilovemyhome.peanotes.common.task.exe.web.TaskResource;

import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static io.muserver.ContextHandlerBuilder.context;
import static io.muserver.openapi.ExternalDocumentationObjectBuilder.externalDocumentationObject;
import static io.muserver.openapi.InfoObjectBuilder.infoObject;


public class StartExecutorLocal {

    public static void main(String[] args) {
        //1. create the executor context
        TaskExecutorContext taskExecutorContext = TaskExecutorContext.builder()
            .withAppName("foo")
            .withContextPath("/foo")
            .withUri(URI.create("http://localhost:12580/foo/task/v1"))
            .withCreateMuServer(false)
            .withListOfAdmin(List.of("http://localhost:10086"))
            .withHandlerBeans(initHandlerBeans())
            .withLogRootPath(Paths.get("d:\\appvol\\temp\\task"))
            .build();
        //2. create the mu server
        //3. Create the executor
        TaskExecutor taskExecutor = TaskExecutor.builder()
            .withTaskExecutorContext(taskExecutorContext)
            .build();
        //4. Start
        taskExecutor.start();
        createAndStartMuServer(12580, taskExecutorContext);
    }

    private static MuServer createAndStartMuServer(int port , TaskExecutorContext context) {
        long start = System.currentTimeMillis();
        LOGGER.info("Create the mu server.");
        String contextPath = "foo";
        MuServerBuilder builder = MuServerBuilder.httpServer()
            .withHttpPort(port)
            .addResponseCompleteListener(info -> {
                MuRequest req = info.request();
                MuResponse resp = info.response();
                LOGGER.info("Response completed: success={}, remoteAddr={}, clientAddress={}, req={}, status={}, duration={}."
                    , info.completedSuccessfully(), req.remoteAddress(), req.clientIP(), req, resp.status(), info.duration());
            })
            .addHandler(context(contextPath)
                .addHandler(createRestHandler(context))
            );
        MuServer muServer = builder.start();
        LOGGER.info("Mu server started in {} ms", System.currentTimeMillis() - start);
        LOGGER.info("Mu server will start app at {}", muServer.uri().resolve("/" + contextPath));
        LOGGER.info("Api url at {}", muServer.uri().resolve("/" + contextPath + "/api.html"));
        return muServer;
    }

    private static RestHandlerBuilder createRestHandler(TaskExecutorContext context) {
        TaskResource taskResource = new TaskResource(context);
        return RestHandlerBuilder
            .restHandler(taskResource)
            .addCustomReader(new JacksonJsonProvider())
            .addCustomWriter(new JacksonJsonProvider())
            .withCollectionParameterStrategy(CollectionParameterStrategy.NO_TRANSFORM)
            .addExceptionMapper(ClientErrorException.class, e -> Response.status(Response.Status.BAD_REQUEST.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("message", e.getMessage()))
                .build())
            .addExceptionMapper(InternalServerErrorException.class, e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("message", e.getMessage()))
                .build())
            .addExceptionMapper(NullPointerException.class, e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("message", e.getMessage()))
                .build())
            .addExceptionMapper(JsonMappingException.class, e -> Response.status(Response.Status.BAD_REQUEST.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("message", e.getMessage()))
                .build())
            .withOpenApiHtmlUrl("/api.html")
            .withOpenApiJsonUrl("/openapi.json")
            .withOpenApiDocument(
                OpenAPIObjectBuilder.openAPIObject()
                    .withInfo(
                        infoObject()
                            .withTitle("User API document")
                            .withDescription("This is a foo task executor.")
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

    private static List<Object> initHandlerBeans() {
        SimpleTask simpleTask = new SimpleTask();
        return List.of(simpleTask);
    }

    private static void initTheTaskAdmin() {

    }


    private static final Logger LOGGER = LoggerFactory.getLogger(StartExecutorLocal.class);
}
