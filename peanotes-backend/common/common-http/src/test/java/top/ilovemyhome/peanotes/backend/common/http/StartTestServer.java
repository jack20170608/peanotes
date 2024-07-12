package top.ilovemyhome.peanotes.backend.common.http;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import io.muserver.Method;
import io.muserver.MuServer;
import io.muserver.rest.RestHandlerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.http.handler.OrderHandler;

import static io.muserver.MuServerBuilder.muServer;

public class StartTestServer {

    //used for local testing
    public static void main(String[] args) {
        MuServer muServer = start();

    }

    public static MuServer start() {
        MuServer muServer = muServer()
            .withHttpsPort(0)
            .addHandler(Method.GET, "/hello", (req, res, map) -> {
                req.headers().entries().forEach(entry -> {
                    res.headers().add(entry.getKey(), entry.getValue());
                });
                res.write("Hello World");
            })
            .addHandler(Method.POST, "/hi", (req, res, map) -> {
                req.headers().entries().forEach(entry -> {
                    res.headers().add(entry.getKey(), entry.getValue());
                });
                LOGGER.info("{}", res.headers());
                res.write("Hi!");
            })
            .addHandler(Method.DELETE, "/del", (req, res, map) -> {
                req.headers().entries().forEach(entry -> {
                    res.headers().add(entry.getKey(), entry.getValue());
                });
                LOGGER.info("{}", res.headers());
                res.write("Delete!");
            })
            .addHandler(RestHandlerBuilder.restHandler(new OrderHandler())
                .addCustomReader(new JacksonJsonProvider())
                .addCustomWriter(new JacksonJsonProvider())
            )
            .start();
        int port = muServer.address().getPort();
        LOGGER.info("Started at {}", muServer.httpsUri());
        return muServer;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(StartTestServer.class);
}
