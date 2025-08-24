package top.ilovemyhome.dag.server.web;

import com.google.common.collect.Maps;
import io.muserver.Cookie;
import io.muserver.MuHandler;
import io.muserver.MuRequest;
import io.muserver.MuResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.commons.common.util.CollectionUtil;
import top.ilovemyhome.dag.server.application.AppContext;
import top.ilovemyhome.dag.server.web.security.JwtAuthenticator;
import top.ilovemyhome.dag.server.web.security.User;
import top.ilovemyhome.peanotes.commons.text.AntPathMatcher;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class SecurityHandler implements MuHandler {

    public SecurityHandler(AppContext appContext) {
        this.contextPath = appContext.getConfig().getString("server.contextPath");
        this.jwtAuthenticator= appContext.getBean("jwtAuthenticator", JwtAuthenticator.class);
        this.cookieName = appContext.getConfig().getString("cookie.name");
        Function<String, List<String>> FUN_RESTRICTED_STATIC_URIS = (contextPath) -> List.of(
            "/" + contextPath + "/index.html"
        );
        this.restrictedStaticUris = FUN_RESTRICTED_STATIC_URIS.apply(this.contextPath);
    }

    @Override
    public boolean handle(MuRequest request, MuResponse response){
        Optional<String> cookieName = request.cookies().stream().map(Cookie::name)
            .filter(name -> StringUtils.startsWith(name, this.cookieName))
            .findAny();
        boolean authenticated = false;
        String path = request.uri().getPath();
        boolean isRestricted = restrictCheck(path);
        if (!isRestricted){
            return false;
        }else {
            if (cookieName.isPresent()){
                String jwtToken = request.cookie(cookieName.get()).orElse(null);
                User user = (User) jwtAuthenticator.authenticate(jwtToken);
                authenticated = Objects.nonNull(user);
            }
            logger.info("Authenticated: [{}].", authenticated);
            if (authenticated) {
                return false;
            }else {
                response.redirect(String.format("/%s/login.html", contextPath));
                return true;
            }
        }
    }

    private boolean restrictCheck(String pathUri){
        restrictedCache.computeIfAbsent(pathUri, (path) -> {
            boolean result = false;
            if (CollectionUtil.isEmpty(restrictedStaticUris)){
                return true;
            }else {
                for (String whitePath : restrictedStaticUris) {
                    if (antPathMatcher.match(whitePath, pathUri)) {
                        result = true;
                        break;
                    }
                }
            }
            return result;
        });
        return restrictedCache.get(pathUri);
    }

    private final Map<String, Boolean> restrictedCache = Maps.newConcurrentMap();

    private final String cookieName;
    private final JwtAuthenticator jwtAuthenticator;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final String contextPath ;
    private final List<String> restrictedStaticUris;

    private static final Logger logger = LoggerFactory.getLogger(SecurityHandler.class);
}
