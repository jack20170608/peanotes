package top.ilovemyhome.peanotes.common.task.exe.common;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import io.muserver.Method;
import io.muserver.MuServer;
import io.muserver.rest.RestHandlerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.Duration;
import java.util.Objects;

import static io.muserver.MuServerBuilder.muServer;

public class FooTaskAdminServer {

    //used for local testing
    public static void main(String[] args) throws Exception {
        FooTaskAdminServer server = new FooTaskAdminServer();
        Thread.sleep(Duration.ofMinutes(15));
        LOGGER.info("Admin server started at uri:[{}].", server.muServer.httpUri());
    }

    public FooTaskAdminServer() {
        MuServer muServer = muServer()
            .withHttpPort(0)
            .addHandler(Method.GET, "/hello", (req, res, map) -> {
                req.headers().entries().forEach(entry -> {
                    res.headers().add(entry.getKey(), entry.getValue());
                });
                res.write("Hello World");
            }).addHandler(RestHandlerBuilder.restHandler(taskAdminController)
                .addCustomReader(new JacksonJsonProvider())
                .addCustomWriter(new JacksonJsonProvider())
            )
            .start();
        LOGGER.info("Mu Server started at {}", muServer.httpUri());
        this.muServer = muServer;
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

    private final MuServer muServer;

    public final TaskAdminController taskAdminController = new TaskAdminController();

    private static final Logger LOGGER = LoggerFactory.getLogger(FooTaskAdminServer.class);
}
