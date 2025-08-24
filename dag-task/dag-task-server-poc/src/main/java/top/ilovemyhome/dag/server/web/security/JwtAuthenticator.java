package top.ilovemyhome.dag.server.web.security;

import java.security.Principal;

public interface JwtAuthenticator {

    String generateJwt(User user);

    Principal authenticate(String token) ;

}
