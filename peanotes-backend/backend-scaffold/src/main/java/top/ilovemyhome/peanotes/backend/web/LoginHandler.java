package top.ilovemyhome.peanotes.backend.web;

import com.typesafe.config.Config;
import io.muserver.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.common.Constants;
import top.ilovemyhome.peanotes.backend.web.security.LoginInfo;
import top.ilovemyhome.peanotes.backend.web.security.SessionHolder;
import top.ilovemyhome.peanotes.backend.web.security.UserAuthInfo;

import java.util.*;

public class LoginHandler implements RouteHandler {

    public LoginHandler(AppContext appContext) {
        this.env = appContext.getEnv();
        Config config = appContext.getConfig();
        this.role = config.getString("requiredRole");

        Objects.requireNonNull(env);
        Objects.requireNonNull(role);
    }


    @Override
    public void handle(MuRequest request, MuResponse response, Map<String, String> pathParams) throws Exception {
        String body = request.readBodyAsString();
        JSONObject bodyJson = new JSONObject(body);
        LoginInfo loginInfo = new LoginInfo(bodyJson.getString("username"), bodyJson.getString("password"));
        UserAuthInfo userAuthInfo = null;
        switch (this.env) {
            case "local", "test", "dev", "sit" -> userAuthInfo = inMemoryAuthenticate(loginInfo);
            case null, default -> throw new IllegalStateException("Unexpected value: " + this.env);
        }
        if (Objects.isNull(userAuthInfo)){
            response.status(Response.Status.UNAUTHORIZED.getStatusCode());
            response.contentType(MediaType.APPLICATION_JSON);
            response.write(failure("Username or Password Error!").toString(1));
        }else if (!userAuthInfo.haveRole(this.role)){
            response.status(Response.Status.FORBIDDEN.getStatusCode());
            response.contentType(MediaType.APPLICATION_JSON);
            response.write(failure("Not allowed to access!").toString(1));
        }else {
            String jSessionId = UUID.randomUUID().toString();
            SessionHolder.set(jSessionId, userAuthInfo);
            response.addCookie(createCookie(jSessionId));
            response.contentType(MediaType.APPLICATION_JSON);
            response.write(success(userAuthInfo.displayName(), "Welcome!!!").toString(1));
        }

    }

    private JSONObject success(String userName, String msg){
        JSONObject res = new JSONObject();
        res.put("success", true);
        res.put("message", msg);
        res.put("userName", userName);
        return res;
    }

    public JSONObject failure(String msg){
        JSONObject res = new JSONObject();
        res.put("success", false);
        res.put("message", msg);
        return res;
    }

    private Cookie createCookie(final String sessionId) {
        return CookieBuilder.newCookie()
            .withName(Constants.SESSION_ID)
            .withValue(sessionId)
            .withMaxAgeInSeconds(2 * 3600)
            .withSameSite("Lax")
            .withPath("*")
            .secure(false)
            .httpOnly(true)
            .build();
    }

    private UserAuthInfo inMemoryAuthenticate(LoginInfo loginInfo) {
        UserAuthInfo userAuthInfo = null;
        String userName = loginInfo.username();
        if (userCredentialMap.containsKey(userName)) {
            if (StringUtils.equals(userCredentialMap.get(userName), DigestUtils.md5Hex(loginInfo.password()))) {
                userAuthInfo = new UserAuthInfo(userIdMap.get(userName), userName, null, userRoleMap.get(userName), null);
            }
        }
        return userAuthInfo;
    }

    private static final Map<String, String> userCredentialMap = Map.of(
        "jack", DigestUtils.md5Hex("1"),
        "jerry", DigestUtils.md5Hex("1"),
        "leo", DigestUtils.md5Hex("1"),
        "tom", DigestUtils.md5Hex("1")

    );
    private static final Map<String, String> userIdMap = Map.of(
        "jack", "1",
        "jerry", "2",
        "leo", "3",
        "tom", "4"
    );
    private static final Map<String, Set<String>> userRoleMap = Map.of(
        "jack", Set.of("rw", "admin", "ro"),
        "jerry", Set.of("rw", "ro"),
        "leo", Set.of("ro"),
        "tom", Set.of()
    );


    private final String role;
    private final String env;
}
