package top.ilovemyhome.peanotes.common.task.admin.web.handlers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/foo")
public class FooHandler {

    @GET
    @Path("/hi/{name}")
    public Response hi(@PathParam("name") String name) {
        return Response.ok("hi " + name).build();
    }
}
