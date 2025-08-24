package top.ilovemyhome.dag.server.web.security;

import io.muserver.rest.Authorizer;

import java.security.Principal;

public class SimpleRoleAuthorizerImpl implements Authorizer {

    @Override
    public boolean isInRole(Principal principal, String role) {
        User userAuthInfo = (User) principal;
        return userAuthInfo.haveRole(role);
    }
}
