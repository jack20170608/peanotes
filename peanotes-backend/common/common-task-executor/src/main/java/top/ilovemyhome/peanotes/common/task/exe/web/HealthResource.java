package top.ilovemyhome.peanotes.common.task.exe.web;

import io.muserver.MuRequest;
import io.muserver.MuResponse;
import io.muserver.RouteHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HealthResource implements RouteHandler {

    public HealthResource() {
        LOGGER.info("Reading metadata from classpath.");
    }

    @Override
    public void handle(MuRequest request, MuResponse response, Map<String, String> map) throws Exception {
        response.status(HttpResponseStatus.OK.code());
        response.contentType(MediaType.APPLICATION_JSON);
        response.sendChunk(this.metaDataStr);
    }

    private final String metaDataStr = """
        {"status" : "true"}
        """;
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthResource.class);

}
