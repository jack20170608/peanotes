package top.ilovemyhome.commons.muserver.security.authorizer;

import io.muserver.rest.Authorizer;
import top.ilovemyhome.commons.muserver.security.core.User;

import java.security.Principal;

public class SimpleRoleAuthorizer implements Authorizer {

    @Override
    public boolean isInRole(Principal principal, String role) {
        User userAuthInfo = (User) principal;
        return userAuthInfo.haveRole(role);
    }
}
