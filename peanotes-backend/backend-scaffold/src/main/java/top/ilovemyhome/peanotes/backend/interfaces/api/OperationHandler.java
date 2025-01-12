package top.ilovemyhome.peanotes.backend.interfaces.api;


import io.muserver.rest.Description;
import io.muserver.rest.Required;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.service.QueryService;
import top.ilovemyhome.peanotes.backend.service.QueryServiceImpl;

import java.util.Map;

@Path("/operation")
@Description(value = "Operation API", details = "Provide the operation entrypoint.")
public class OperationHandler {


    public OperationHandler(AppContext appContext) {
        QueryService queryService = appContext.getBean("queryService", QueryServiceImpl.class);
    }

    @GET
    @Path("/hello")
    @Produces(MediaType.APPLICATION_JSON)
    @Description(value = "It's a hello world endpoint", details = "hello world")
    public Response getById(
        @Description("Your name")
        @Required
        @QueryParam("name") String name) {
        Response response;
        if (StringUtils.isBlank(name)) {
            LOGGER.warn("Blank name provided");
            throw new ClientErrorException("Blank name provided", Response.Status.BAD_REQUEST);
        }
        return Response.ok()
            .header("Content-Type", MediaType.APPLICATION_JSON)
            .entity(Map.of("data", "hello " + name))
            .build();
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(OperationHandler.class);

}

