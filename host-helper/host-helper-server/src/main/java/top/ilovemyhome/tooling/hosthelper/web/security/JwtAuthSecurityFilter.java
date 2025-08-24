package top.ilovemyhome.tooling.hosthelper.web.security;

import io.muserver.rest.Authorizer;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.commons.common.util.CollectionUtil;
import top.ilovemyhome.tooling.hosthelper.web.security.core.AuthType;
import top.ilovemyhome.tooling.hosthelper.web.security.core.MuServerSecurityContext;

import java.security.Principal;
import java.util.*;

import static top.ilovemyhome.tooling.hosthelper.util.SharedConstants.AUTH_HEADER_NAME;

public class JwtAuthSecurityFilter implements ContainerRequestFilter {

    public JwtAuthSecurityFilter(JwtAuthenticator jwtAuthenticator, Authorizer authorizer, String cookieName) {
        this.jwtAuthenticator = jwtAuthenticator;
        this.authorizer = authorizer;
        this.cookieName = cookieName;
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        List<String> jwtTokens = extractJwtTokens(containerRequestContext);
        if (CollectionUtil.isEmpty(jwtTokens)){
            containerRequestContext.abortWith(Response.status(401).entity("401 Unauthorized!").type(MediaType.TEXT_PLAIN).build());
            return;
        }
        String jwtToken = jwtTokens.get(0);
        Principal principal = jwtAuthenticator.authenticate(jwtToken);
        boolean isHttps = "https".equalsIgnoreCase(containerRequestContext.getUriInfo().getRequestUri().getScheme());
        SecurityContext securityContext;
        if (Objects.isNull(principal)){
            containerRequestContext.abortWith(Response.status(401).entity("401 Unauthorized!").type(MediaType.TEXT_PLAIN).build());
        }else {
            securityContext = isHttps ? new MuServerSecurityContext(principal, authorizer, isHttps, SecurityContext.BASIC_AUTH)
                : new MuServerSecurityContext(principal, authorizer, isHttps, SecurityContext.BASIC_AUTH);
            containerRequestContext.setSecurityContext(securityContext);
        }
    }

    private List<String> extractJwtTokens(ContainerRequestContext containerRequestContext){
        List<String> jwtTokens = new ArrayList<>();
        Map<String, Cookie> cookieMap = containerRequestContext.getCookies();
        for (Map.Entry<String, Cookie> entry : cookieMap.entrySet()) {
            if (entry.getKey().startsWith(cookieName)) {
                Cookie cookie = entry.getValue();
                if (cookie != null) {
                    jwtTokens.add(cookie.getValue());
                    break;
                }
            }
        }
        if (jwtTokens.isEmpty()){
            String jwtToken = containerRequestContext.getHeaderString(AUTH_HEADER_NAME);
            if (jwtToken!= null && jwtToken.startsWith(AuthType.BEARER.getPrefix())){
                jwtTokens.add(jwtToken.substring(AuthType.BEARER.getPrefix().length()));
            }
        }
        return jwtTokens;
    }

    private final String cookieName;
    private final JwtAuthenticator jwtAuthenticator;
    private final Authorizer authorizer;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthSecurityFilter.class);
}
