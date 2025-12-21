package top.ilovemyhome.commons.muserver.security.authenticator;

import top.ilovemyhome.commons.muserver.security.core.User;

public interface JwtAuthenticator extends TokenAuthenticator {

    String generateJwt(User user);

}
