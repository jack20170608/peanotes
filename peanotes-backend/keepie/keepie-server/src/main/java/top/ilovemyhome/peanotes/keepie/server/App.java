package top.ilovemyhome.peanotes.keepie.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    public static void main(String[] args) {
        LOGGER.trace("Hi, trace");
        LOGGER.debug("Hi, Debug.");
        LOGGER.info("Hi, Info.");
        LOGGER.warn("Hi, warn.");
        LOGGER.error("Hi, Info.");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
}
