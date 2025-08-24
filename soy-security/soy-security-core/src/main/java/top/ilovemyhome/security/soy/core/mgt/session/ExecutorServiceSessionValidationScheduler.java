package top.ilovemyhome.security.soy.core.mgt.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * SessionValidationScheduler implementation that uses a
 * {@link ScheduledExecutorService} to call {@link ValidatingSessionManager#validateSessions()} every
 * <em>{@link #getSessionValidationInterval sessionValidationInterval}</em> milliseconds.
 *
 * @since 0.9
 */
public class ExecutorServiceSessionValidationScheduler implements SessionValidationScheduler, Runnable {

    //TODO - complete JavaDoc

    /**
     * Private internal log instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorServiceSessionValidationScheduler.class);

    ValidatingSessionManager sessionManager;
    private ScheduledExecutorService service;
    private long sessionValidationInterval = DefaultSessionManager.DEFAULT_SESSION_VALIDATION_INTERVAL;
    private boolean enabled;
    private String threadNamePrefix = "SessionValidationThread-";

    public ExecutorServiceSessionValidationScheduler() {
        super();
    }

    public ExecutorServiceSessionValidationScheduler(ValidatingSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public ValidatingSessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(ValidatingSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public long getSessionValidationInterval() {
        return sessionValidationInterval;
    }

    public void setSessionValidationInterval(long sessionValidationInterval) {
        this.sessionValidationInterval = sessionValidationInterval;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public String getThreadNamePrefix() {
        return this.threadNamePrefix;
    }

    /**
     * Creates a single thread {@link ScheduledExecutorService} to validate sessions at fixed intervals
     * and enables this scheduler. The executor is created as a daemon thread to allow JVM to shut down
     */
    //TODO Implement an integration test to test for jvm exit as part of the standalone example
    // (so we don't have to change the unit test execution model for the core module)
    public void enableSessionValidation() {
        if (this.sessionValidationInterval > 0L) {
            this.service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                private final AtomicInteger count = new AtomicInteger(1);

                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    thread.setName(threadNamePrefix + count.getAndIncrement());
                    return thread;
                }
            });
            this.service.scheduleAtFixedRate(this, sessionValidationInterval,
                    sessionValidationInterval, TimeUnit.MILLISECONDS);
        }
        this.enabled = true;
    }

    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Executing session validation...");
        }
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
            LOGGER.error("Error while validating the session, the thread will be stopped and session validation disabled", e);
            this.disableSessionValidation();
        });
        long startTime = System.currentTimeMillis();
        try {
            this.sessionManager.validateSessions();
        } catch (RuntimeException e) {
            LOGGER.error("Error while validating the session", e);
            //we don't stop the thread
        }
        long stopTime = System.currentTimeMillis();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Session validation completed successfully in " + (stopTime - startTime) + " milliseconds.");
        }
    }

    public void disableSessionValidation() {
        if (this.service != null) {
            this.service.shutdownNow();
        }
        this.enabled = false;
    }
}
