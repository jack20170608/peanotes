package top.ilovemyhome.benchmark.server.web.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import top.ilovemyhome.benchmark.server.application.AppContext;

@Path("/foo")
public class FooUserHandler {

    private final AppContext appContext;

    public FooUserHandler(AppContext appContext) {
        this.appContext = appContext;
    }

    @GET
    @Path("/hi")
    @Produces(MediaType.TEXT_PLAIN)
    public Response hi(@Context SecurityContext context, @QueryParam("name") String name) {
        if (!context.isUserInRole("read")) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok("hi, " + name).build();
    }
}
