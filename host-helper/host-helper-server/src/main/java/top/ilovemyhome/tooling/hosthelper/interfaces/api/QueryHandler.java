package top.ilovemyhome.tooling.hosthelper.interfaces.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.commons.jdbi.page.Direction;
import top.ilovemyhome.peanotes.commons.jdbi.page.Page;
import top.ilovemyhome.peanotes.commons.jdbi.page.impl.PageRequest;
import top.ilovemyhome.tooling.hosthelper.application.AppContext;
import top.ilovemyhome.tooling.hosthelper.domain.FileSearchCriteria;
import top.ilovemyhome.tooling.hosthelper.domain.FileSearchResult;
import top.ilovemyhome.tooling.hosthelper.service.QueryService;

@Path("/query/api/v1")
public class QueryHandler {

    @GET
    @Path("/allHosts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllHosts(@Context SecurityContext securityContext) {
        if (!securityContext.isUserInRole("read")) {
            throw new ClientErrorException("This requires a User role", 403);
        }
        return Response.ok(queryService.getAllHosts())
            .build();
    }

    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response search(@QueryParam("page") @DefaultValue("0") int page
        , @QueryParam("pageSize") @DefaultValue("20") int pageSize
        , @QueryParam("sortBy") @DefaultValue("name") String sortBy
        , @QueryParam("direction") @DefaultValue("ASC") Direction direction
        , FileSearchCriteria searchCriteria) {
        PageRequest pageRequest = new PageRequest(page, pageSize, direction, sortBy);
        Page<FileSearchResult> result = queryService.search(searchCriteria, pageRequest);
        return Response.ok().entity(result)
           .build();
    }

    public QueryHandler(AppContext appContext) {
        this.queryService = appContext.getBean("queryService", QueryService.class);
    }

    private final QueryService queryService;

    private static final Logger logger = LoggerFactory.getLogger(QueryHandler.class);
}
