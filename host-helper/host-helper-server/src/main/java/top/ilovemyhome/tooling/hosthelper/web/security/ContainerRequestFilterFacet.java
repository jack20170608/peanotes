package top.ilovemyhome.tooling.hosthelper.web.security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.commons.common.util.CollectionUtil;
import top.ilovemyhome.peanotes.commons.text.AntPathMatcher;
import top.ilovemyhome.tooling.hosthelper.web.security.core.AuthType;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static top.ilovemyhome.tooling.hosthelper.util.SharedConstants.AUTH_HEADER_NAME;

public class ContainerRequestFilterFacet implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (Objects.isNull(basicAuthFiler) && Objects.isNull(bearerAuthFiler)){
            return;
        }
        String path = requestContext.getUriInfo().getRequestUri().getPath();
        if (checkWhiteList(path)){
            return;
        }
        //1. check if contain the auth header
        String authHeader = requestContext.getHeaderString(AUTH_HEADER_NAME);
        AuthType authType = AuthType.fromHeader(authHeader);

        //By default use the Baerer Auth
        if (Objects.requireNonNull(authType) == AuthType.BASIC) {
            doBasicAuth(requestContext);
        } else {
            doBearerAuth(requestContext);
        }
    }


    private void doBasicAuth(ContainerRequestContext requestContext) {
        try {
            if(Objects.isNull(basicAuthFiler)){
                throw new IllegalStateException("No Basic Auth Filter is configured");
            }
            basicAuthFiler.filter(requestContext);
        }catch (Throwable e){
            requestContext.abortWith(RES_UNAUTHORIZED_FUN.apply(e.getMessage()));
        }
    }

    private void doBearerAuth(ContainerRequestContext requestContext) {
        try {
            if (Objects.isNull(bearerAuthFiler)){
                throw new IllegalStateException("No Bearer Auth Filter is configured");
            }
            bearerAuthFiler.filter(requestContext);
        }catch (Throwable e){
            requestContext.abortWith(RES_UNAUTHORIZED_FUN.apply(e.getMessage()));
        }
    }

    private static final Function<String, Response> RES_UNAUTHORIZED_FUN
        = (msg) -> Response.status(Response.Status.UNAUTHORIZED).entity(msg).build();

    private static final Function<String, Response> RES_FORBIDDEN_FUN
        = (msg) -> Response.status(Response.Status.FORBIDDEN).entity(msg).build();


    public ContainerRequestFilterFacet(List<String> whiteList
        , ContainerRequestFilter basicAuthFiler
        , ContainerRequestFilter bearerAuthFiler) {
        if (!CollectionUtil.isEmpty(whiteList)){
            this.whiteList = List.copyOf(whiteList);
        }
        if (Objects.isNull(basicAuthFiler) && Objects.isNull(bearerAuthFiler)){
            logger.warn("No authentication filter is configured");
        }
        this.basicAuthFiler = basicAuthFiler;
        this.bearerAuthFiler = bearerAuthFiler;
    }

    private boolean checkWhiteList(String pathUri){
        boolean result = false;
        if (CollectionUtil.isEmpty(whiteList)){
            return true;
        }
        for (String whitePath : whiteList){
            if (antPathMatcher.match(whitePath, pathUri)){
                result = true;
                break;
            }
        }
        return result;
    }

    //The default relationship is or
    private ContainerRequestFilter basicAuthFiler;
    private ContainerRequestFilter bearerAuthFiler;
    private List<String> whiteList;


    //todo need to check if the AntPathMatcher is threadsafe or not
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private static final Logger logger = LoggerFactory.getLogger(ContainerRequestFilterFacet.class);
}
