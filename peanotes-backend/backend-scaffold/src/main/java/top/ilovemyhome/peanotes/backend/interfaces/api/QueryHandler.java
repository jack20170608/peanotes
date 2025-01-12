package top.ilovemyhome.peanotes.backend.interfaces.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.dao.QueryDao;
import top.ilovemyhome.peanotes.backend.domain.QueryResultV1;
import top.ilovemyhome.peanotes.backend.domain.QueryResultV2;
import top.ilovemyhome.peanotes.backend.domain.ResultFormat;

@Path("/query")
public class QueryHandler {

    public QueryHandler(AppContext appContext) {
        queryDao = appContext.getBean("queryDao", QueryDao.class);
    }


    @POST
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response query(@QueryParam("format") @DefaultValue("JSON") ResultFormat resultFormat,  String sql) {
        switch (resultFormat){
            case JSON -> {
                QueryResultV1 resultV1 = queryDao.queryV1(sql);
                return Response.ok(resultV1).build();
            }
            case TEXT -> {
                QueryResultV2 resultV2 = queryDao.queryV2(sql);
                return Response.ok(resultV2).build();
            }
            case null, default -> {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
    }

    private final QueryDao queryDao;
}
