package top.ilovemyhome.peanotes.backend.web.handlers;


import io.muserver.rest.Description;
import io.muserver.rest.Required;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Direction;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Page;
import top.ilovemyhome.peanotes.backend.dao.operation.OperationLogSearchCriteria;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.impl.PageRequest;
import top.ilovemyhome.peanotes.backend.domain.operation.OperationLogEntity;
import top.ilovemyhome.peanotes.backend.service.operation.OperationLogCrudService;

import java.util.List;
import java.util.Map;

@Path("/operation/api/v1")
@Description(value = "Operation API", details = "Provide the operation entrypoint.")
public class OperationHandler {


    public OperationHandler(AppContext appContext) {
        operationLogCrudService = appContext.getBean("operationLogCrudService", OperationLogCrudService.class);
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

    @POST
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Description(value = "Prove the dao level is working.")
    public Response query(OperationLogSearchCriteria searchCriteria) {
        try {
            List<OperationLogEntity> result = operationLogCrudService.find(searchCriteria);
            LOGGER.info("Result size={}.", result.size());
            return Response.ok(result).build();
        } catch (Exception e) {
            LOGGER.warn("Error", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/queryWithPage")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Description(value = "Prove the dao level is working with pagerequest.")
    public Response queryWithPage(@QueryParam("page") @DefaultValue("0") int page
        , @QueryParam("pageSize") @DefaultValue("100") int pageSize
        , @QueryParam("sortBy") @DefaultValue("ID") String sortBy
        , @QueryParam("direction") @DefaultValue("ASC") Direction direction
        , OperationLogSearchCriteria searchCriteria) {
        try {
            PageRequest pageRequest = new PageRequest(page, pageSize, direction, sortBy);
            Page<OperationLogEntity> result = operationLogCrudService.find(searchCriteria, pageRequest);
            LOGGER.info("Result size={}.", result.getSize());
            return Response.ok(result).build();
        } catch (Exception e) {
            LOGGER.warn("Error", e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private final OperationLogCrudService operationLogCrudService;
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationHandler.class);

}

