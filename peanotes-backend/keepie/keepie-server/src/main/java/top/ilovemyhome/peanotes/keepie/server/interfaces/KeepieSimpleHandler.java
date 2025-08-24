package top.ilovemyhome.peanotes.keepie.server.interfaces;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import top.ilovemyhome.peanotes.keepie.server.AppContext;
import top.ilovemyhome.peanotes.backend.common.http.RestClient;

@Path("/secrets")
public class KeepieSimpleHandler {

    @GET
    @Path("/hi")
    public Response getSecrets() {
        return Response.ok("hello").build();
    }

    public KeepieSimpleHandler(AppContext appContext) {
        restClient = null;
    }


    private final RestClient restClient;
}
