package top.ilovemyhome.peanotes.keepie.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
//import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

import java.sql.SQLException;

public class Loggerutils {

    public static void main(String[] args) {
        costomiseLogger("foo.log");
        logger.trace("tracing..............");
        logger.debug("debuging.........");
        logger.info("infoing.........");
        logger.warn("warning.........");
        logger.error("Erroring.........", new SQLException("Mocked exception."));
    }

    private static Logger costomiseLogger(String file) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
        ple.setContext(lc);
        ple.start();
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setFile(file);
        fileAppender.setEncoder(ple);
        fileAppender.setContext(lc);
        fileAppender.start();

        ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
        logbackLogger.addAppender(fileAppender);
        logbackLogger.setLevel(Level.TRACE);
        logbackLogger.setAdditive(true); /* set to true if root should log too */

        return logger;
    }


    private static final Logger logger = LoggerFactory.getLogger(Loggerutils.class);


}
