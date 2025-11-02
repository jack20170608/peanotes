package top.ilovemyhome.commons.bin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static top.ilovemyhome.commons.bin.MandatorySystemProperties.*;

//The application lifecycle management
public final class RunUtils {

    private static final Logger logger = LoggerFactory.getLogger(RunUtils.class);

    private static final Path logHome;
    private static final String appName;
    private static final String procName;
    private static final Path pidFile;
    private static final Path stateFile;

    static {
        logHome = Path.of(Objects.requireNonNull(System.getProperty(LOG_HOME.getDotName())));
        appName = Objects.requireNonNull(System.getProperty(APP_NAME.getDotName()));
        procName = Objects.requireNonNull(System.getProperty(PROC_NAME.getDotName()));
        pidFile = Path.of(Objects.requireNonNull(System.getProperty(PID_FILE.getDotName())));
        stateFile = Path.of(Objects.requireNonNull(System.getProperty(STATE_FILE.getDotName())));
        if (!isDirectory(logHome)) {
            logger.warn("LOG_HOME: [{}] is not a directory", logHome);
            throw new RuntimeException("LOG_HOME: [" + logHome + "] is not a directory");
        }
        if (isFile(pidFile)) {
            logger.warn("PID_FILE: [{}] is not a file", pidFile);
            throw new RuntimeException("PID_FILE: [" + pidFile + "] is not a file");
        }
        if (isFile(stateFile)) {
            logger.warn("STATE_FILE: [{}] is not a file", stateFile);
            throw new RuntimeException("STATE_FILE: [" + stateFile + "] is not a file");
        }
    }

    public static void preRun() {
        logger.info("Starting app with LOG_HOME: [{}], APP_NAME: [{}], PROC_NAME: [{}], PID_FILE: [{}], STATE_FILE: [{}]"
            , logHome, appName, procName, pidFile, stateFile);
        updatePidFile();
        updateStateFile(AppStatus.STARTING);
    }


    public static void markStartSuccess() {
        logger.info("App with name [{}] has been started.", appName);
        updateStateFile(AppStatus.RUNNING);
    }

    public static void markStartFailed() {
        logger.info("App with name [{}] has been failed to start.", appName);
        updateStateFile(AppStatus.START_FAILED);
    }

    public static void preStop(){
        logger.info("Stopping app with LOG_HOME: [{}], APP_NAME: [{}], PROC_NAME: [{}], PID_FILE: [{}], STATE_FILE: [{}]"
            , logHome, appName, procName, pidFile, stateFile);
        updateStateFile(AppStatus.STOPPING);
    }

    public static void markStopSuccess() {
        logger.info("App with name [{}] has been stopped normally.", appName);
        updateStateFile(AppStatus.STOPPED);
    }

    public static void markStopFailed() {
        logger.info("App with name [{}] has been failed to stop.", appName);
        updateStateFile(AppStatus.STOP_FAILED);
    }

    private static void updatePidFile() {
        try {
            Files.writeString(pidFile, String.valueOf(ProcessHandle.current().pid()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to write pid file: " + pidFile, e);
        }
    }

    private static void updateStateFile(AppStatus appStatus) {
        try {
            Files.writeString(stateFile, appStatus.name());
        } catch (Exception e) {
            throw new RuntimeException("Failed to write state file: " + stateFile, e);
        }
    }

    private static boolean emptyOrBlank(String string) {
        return string == null || string.isEmpty() || string.trim().isEmpty();
    }

    public static boolean isDirectory(Path dirPath) {
        return Files.exists(dirPath) && Files.isDirectory(dirPath);
    }

    public static boolean isFile(Path filePath) {
        return !Files.exists(filePath) || !Files.isRegularFile(filePath);
    }

}
