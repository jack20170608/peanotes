package top.ilovemyhome.peanotes.gateway.web.handlers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/foo")
public class FooHandler {

    @GET
    @Path("/bar")
    public Response bar(@QueryParam("name") String name) {
        return Response.ok("Hi, " + name + "!")
            .build();
    }

}
