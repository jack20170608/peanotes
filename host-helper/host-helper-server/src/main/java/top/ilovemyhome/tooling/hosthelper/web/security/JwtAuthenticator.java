package top.ilovemyhome.tooling.hosthelper.web.security;

import java.security.Principal;

public interface JwtAuthenticator {

    String generateJwt(User user);

    Principal authenticate(String token) ;

}
