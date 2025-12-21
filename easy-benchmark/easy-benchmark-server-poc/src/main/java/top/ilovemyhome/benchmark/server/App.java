package top.ilovemyhome.benchmark.server;

import io.muserver.MuServer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.benchmark.server.application.WebServerBootstrap;
import top.ilovemyhome.benchmark.server.application.AppContext;

public class App {

    public static void main(String[] args) {
        logger.info("Starting application.");
        String env = System.getProperty("env");
        if (StringUtils.isBlank(env)){
            throw new IllegalStateException("Cannot find env property.");
        }
        App app = new App();
        app.appContext = AppContext.getInstance();
        MuServer muServer = WebServerBootstrap.start(app.appContext);

        logger.info("Server started at {}", muServer.uri());
    }


    public AppContext getAppContext() {
        return appContext;
    }

    private AppContext appContext;
    private static final Logger logger = LoggerFactory.getLogger(App.class);
}
