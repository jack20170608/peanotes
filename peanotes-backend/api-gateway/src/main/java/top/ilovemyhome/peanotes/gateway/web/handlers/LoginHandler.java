package top.ilovemyhome.peanotes.gateway.web.handlers;

import io.jsonwebtoken.Jwts;
import io.muserver.rest.Description;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;

@Path("/sys")
public class LoginHandler {


    @GET
    @Path("/login/api/v1")
    @Description("The system login endpoint")
    public Response loginV1(@QueryParam("username") final String username
        , @QueryParam("password") final String password) {
        if("jack".equals(username) && "1".equals(password)) {
            SecretKey key = Jwts.SIG.HS256.key().build();
            String jws = Jwts.builder().subject("Joe").signWith(key).compact();
            return Response.ok(jws).build();
        }
        return Response.status(Response.Status.FORBIDDEN)
            .entity("Not Allowed").build();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginHandler.class);
}
