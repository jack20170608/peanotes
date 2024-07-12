package top.ilovemyhome.peanotes.gateway.web.handlers;

import io.muserver.MuRequest;
import io.muserver.MuResponse;
import io.muserver.RouteHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.resource.ResourceUtil;

import java.util.Map;

public class HealthHandler implements RouteHandler {

    public HealthHandler() {
        LOGGER.info("Reading metadata from classpath.");
        this.metaDataStr = ResourceUtil.getClasspathResourceAsString("metadata.json");
    }

    @Override
    public void handle(MuRequest request, MuResponse response, Map<String, String> map) throws Exception {
        response.status(HttpResponseStatus.OK.code());
        response.contentType(MediaType.APPLICATION_JSON);
        response.sendChunk(this.metaDataStr);
    }

    private final String metaDataStr;
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthHandler.class);

}
