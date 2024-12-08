package top.ilovemyhome.peanotes.common.task.exe.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.text.StrUtils;
import top.ilovemyhome.peanotes.common.task.exe.*;
import top.ilovemyhome.peanotes.common.task.exe.domain.RegistryParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.TaskResponse;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.RegistType;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public interface RegistryProcessor extends LifeCycle {

    default boolean isRegistered() {
        return false;
    }

    static Builder builder() {
        return new Builder();
    }

    class Builder {
        private TaskExecutor taskExecutor;

        public RegistryProcessor.Builder withTaskExecutor(TaskExecutor taskExecutor) {
            this.taskExecutor = taskExecutor;
            return this;
        }

        public RegistryProcessor build() {
            return new RegistryProcessorImpl(taskExecutor);
        }
    }
}

class RegistryProcessorImpl implements RegistryProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryProcessorImpl.class);
    private Thread registryThread;

    //The state atomic
    private final AtomicReference<State> stateRef = new AtomicReference<>(State.INITIALIZED);

    private volatile boolean stopFlag = false;
    private final TaskExecutor taskExecutor;
    private final TaskExecutorContext taskExecutorContext;
    private volatile boolean registered = false;

    RegistryProcessorImpl(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
        this.taskExecutorContext = taskExecutor.getContext();
    }

    @Override
    public void start() {
        if (stateRef.compareAndSet(State.INITIALIZED, State.STARTING)) {
            try {
                validate();
                doStart();
            } catch (Throwable throwable) {
                stateRef.set(State.STOPPED);
                throw throwable;
            }
            stateRef.set(State.STARTED);
        }
    }

    @Override
    public void stop() {
        stop(Duration.ofHours(2));
    }

    @Override
    public void stop(Duration timeoutDuration) {
        //in case it is shut down more than once at the same time.
        if (stateRef.compareAndSet(State.STARTED, State.STOPPING)) {
            stopFlag = true;
            if (registryThread != null) {
                registryThread.interrupt();
                try {
                    registryThread.join();
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            stateRef.set(State.STOPPED);
        }
    }

    @Override
    public State getState() {
        return stateRef.get();
    }

    @Override
    public boolean isRegistered() {
        return registered;
    }

    private void validate() {
        String appName = taskExecutorContext.getAppName();
        // valid
        if (StrUtils.isEmpty(appName)) {
            LOGGER.error("Executor registry config fail, app name is null.");
            throw new IllegalArgumentException("App name is null");
        }
        if (taskExecutor.getTaskAdmins() == null || taskExecutor.getTaskAdmins().isEmpty()) {
            LOGGER.error("Executor registry config fail, adminAddresses is null.");
            throw new IllegalArgumentException("Admin addresses is null");
        }
        URI uri = taskExecutorContext.uri();
        if (Objects.isNull(uri)) {
            LOGGER.error("Executor registry config fail, the uri is null.");
            throw new IllegalArgumentException("uri is null");
        }
    }

    private void doStart() {
        String appName = taskExecutorContext.getAppName();
        final URI uri = taskExecutorContext.uri();
        registryThread = new Thread(() -> {
            long sleepTime = 50L;
            // registry
            while (!stopFlag) {
                try {
                    TaskAdmin registeredTo = null;
                    RegistryParam registryParam = new RegistryParam(RegistType.EXECUTOR, appName, uri.toString());
                    for (TaskAdmin admin : taskExecutor.getTaskAdmins()) {
                        try {
                            TaskResponse registryResult = admin.register(registryParam);
                            if (registryResult != null && TaskResponse.SUCCESS.code() == registryResult.code()) {
                                LOGGER.debug("Registry success, registryParam:{}, registryResult:{}", registryParam, registryResult);
                                registeredTo = admin;
                                break;
                            } else {
                                LOGGER.info("Registry fail, registryParam:{}, registryResult:{}", registryParam, registryResult);
                            }
                        } catch (Exception e) {
                            LOGGER.info("Registry error, registryParam:{}", registryParam, e);
                        }
                    }
                    if (Objects.nonNull(registeredTo)) {
                        LOGGER.info("Registered to address=[{}]!", registeredTo.getAdminServerurl());
                        this.registered = true;
                    } else {
                        LOGGER.warn("Registered failed for all address!");
                    }
                } catch (Exception e) {
                    if (!stopFlag) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                try {
                    if (!stopFlag) {
                        if (this.registered) {
                            sleepTime = Constants.BEAT_TIMEOUT_MILLISECONDS;
                        } else {
                            if (sleepTime <= Constants.BEAT_TIMEOUT_MILLISECONDS) {
                                //Retry fast
                                sleepTime = sleepTime * 2;
                            } else {
                                sleepTime = Constants.BEAT_TIMEOUT_MILLISECONDS;
                            }
                        }
                        TimeUnit.MILLISECONDS.sleep(sleepTime);
                    }
                } catch (InterruptedException e) {
                    if (!stopFlag) {
                        LOGGER.warn("Executor registry thread interrupted, error msg:{}", e.getMessage());
                    }
                }
            }

            // registry remove, if the remote admin is not
            TaskAdmin unRegisteredAdmin = null;
            try {
                RegistryParam registryParam = new RegistryParam(RegistType.EXECUTOR, appName, uri.toString());
                for (TaskAdmin admin : taskExecutor.getTaskAdmins()) {
                    try {
                        TaskResponse registryResult = admin.unRegister(registryParam);
                        if (registryResult != null && TaskResponse.SUCCESS.code() == registryResult.code()) {
                            LOGGER.debug("Registry-remove success, registryParam:{}, registryResult:{}", registryParam, registryResult);
                            unRegisteredAdmin = admin;
                            break;
                        } else {
                            LOGGER.debug("Registry-remove fail, registryParam:{}, registryResult:{}", registryParam, registryResult);
                        }
                    } catch (Exception e) {
                        LOGGER.info("Registry-remove error, registryParam:{}", registryParam, e);
                    }
                }
                if (Objects.isNull(unRegisteredAdmin)) {
                    LOGGER.error("Registry-remove error,registryParam:{} ", registryParam);
                }else {
                    this.registered = false;
                    LOGGER.debug("Registry-remove success,registryParam:{}.", registryParam);
                }
            } catch (Exception e) {
                if (!stopFlag) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            LOGGER.info("Executor registry thread destroy.");
        });
        registryThread.setDaemon(true);
        registryThread.setName("ExecutorRegistryThread");
        registryThread.start();
    }
}
