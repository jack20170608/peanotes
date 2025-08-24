package top.ilovemyhome.dag.server.application;

import com.typesafe.config.Config;
import io.muserver.rest.Authorizer;
import io.muserver.rest.BasicAuthSecurityFilter;
import io.muserver.rest.UserPassAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.dag.server.web.security.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public final class AppContext {

    public AppContext(String env, Config config) {
        this.env = env;
        this.config = config;
    }



    public synchronized void init() {
        logger.info("Init the application context..........");
        //0. init the host list
        //1. init the security
        initSecurity();

        //2.dao
        initDao();

        //3. service
        initService();

    }

    private void initSecurity(){
        logger.info("Init the security..........");
        List<User> users = config.getConfigList("users")
            .stream()
            .map(item -> {
                String id = item.getString("id");
                String name = item.getString("name");
                String displayName = item.getString("displayName");
                List<String> roles = item.getStringList("roles");
                String passwordHashVal = item.getString("passwordHashVal");
                Map<String, String> attributes = item.getConfig("attributes").root().unwrapped()
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> String.valueOf(e.getValue())
                    ));
                return new User(id, name, displayName, roles, passwordHashVal, attributes);
            })
            .toList();


        InMemoryUserPassAuthenticator userPassAuthenticator
            = new InMemoryUserPassAuthenticator(users);

        JwtAuthenticator jwtAuthenticator = new SimpleJwtAuthenticatorImpl(
            config.getString("jwt.publicKeyLocation")
            , config.getString("jwt.privateKeyLocation"));

        Authorizer authorizer = new SimpleRoleAuthorizerImpl();
        BasicAuthSecurityFilter basicAuthSecurityFilter = new BasicAuthSecurityFilter(
            "/"
            , userPassAuthenticator
            , authorizer
        );
        JwtAuthSecurityFilter jwtAuthSecurityFilter = new JwtAuthSecurityFilter(
            jwtAuthenticator
            , authorizer
            , config.getString("cookie.name")
        );
        ContainerRequestFilterFacet containerRequestFilterFacet = new ContainerRequestFilterFacet(
            config.getStringList("security.whiteList")
            , basicAuthSecurityFilter
            , jwtAuthSecurityFilter
        );
        BEAN_NAME_FACTORY.put("jwtAuthenticator", jwtAuthenticator);
        BEAN_FACTORY.put(JwtAuthenticator.class, jwtAuthenticator);
        BEAN_NAME_FACTORY.put("userPassAuthenticator", userPassAuthenticator);
        BEAN_FACTORY.put(UserPassAuthenticator.class, userPassAuthenticator);
        BEAN_FACTORY.put(ContainerRequestFilterFacet.class, containerRequestFilterFacet);
        BEAN_NAME_FACTORY.put("containerRequestFilterFacet", containerRequestFilterFacet);
        BEAN_FACTORY.put(BasicAuthSecurityFilter.class, basicAuthSecurityFilter);
        BEAN_NAME_FACTORY.put("basicAuthSecurityFilter", basicAuthSecurityFilter);
        BEAN_FACTORY.put(JwtAuthSecurityFilter.class, jwtAuthSecurityFilter);
        BEAN_NAME_FACTORY.put("jwtAuthSecurityFilter", jwtAuthSecurityFilter);
    }


    private void initDao() {
    }

    private void initService() {
    }

    public String getApplicationName() {
        return config.getString("name");
    }

    public String getEnv() {
        return env;
    }


    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName, Class<T> beanClass) {
        return (T) BEAN_FACTORY.getOrDefault(beanClass, (T) BEAN_NAME_FACTORY.get(beanName));
    }

    private final String env;
    private final Config config;
    private final Map<Class<?>, Object> BEAN_FACTORY = new HashMap<>();
    private final Map<String, Object> BEAN_NAME_FACTORY = new HashMap<>();

    public Config getConfig() {
        return config;
    }

    private static final Logger logger = LoggerFactory.getLogger(AppContext.class);
}
