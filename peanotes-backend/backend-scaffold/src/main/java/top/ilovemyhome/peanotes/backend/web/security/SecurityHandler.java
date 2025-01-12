package top.ilovemyhome.peanotes.backend.web.security;

import io.muserver.MuHandler;
import io.muserver.MuRequest;
import io.muserver.MuResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.common.Constants;

import java.util.Optional;

public class SecurityHandler implements MuHandler {

    public SecurityHandler(AppContext appContext) {
        this.contextPath = appContext.getConfig().getString("server.contextPath");
    }

    @Override
    public boolean handle(MuRequest request, MuResponse response) throws Exception {
        Optional<String> sessionId = request.cookie(Constants.SESSION_ID);
        boolean authenticated = false;
        if (sessionId.isPresent()) {
            UserAuthInfo userAuthInfo = SessionHolder.get(sessionId.get());
            if (userAuthInfo != null) {
                authenticated = true;
            }
        }
        log.info("Authenticated: {}", authenticated);
        if (authenticated) {
            return false;
        }else {
            response.redirect(String.format("/%s/login.html", contextPath));
            return true;
        }
    }

    private final String contextPath ;
    private static final Logger log = LoggerFactory.getLogger(SecurityHandler.class);
}
