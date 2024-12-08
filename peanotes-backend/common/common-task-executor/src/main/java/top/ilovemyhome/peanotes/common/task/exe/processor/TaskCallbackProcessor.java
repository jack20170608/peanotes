package top.ilovemyhome.peanotes.common.task.exe.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.common.utils.CollectionUtil;
import top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils;
import top.ilovemyhome.peanotes.common.task.exe.*;
import top.ilovemyhome.peanotes.common.task.exe.domain.HandleCallbackParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.TaskResponse;
import top.ilovemyhome.peanotes.common.task.exe.helper.TaskExecutorHelper;
import top.ilovemyhome.peanotes.common.task.exe.helper.TaskHelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.UNSIGNED_TIMESTAMP_PATTERN;

public interface TaskCallbackProcessor extends LifeCycle {

    void pushCallBack(HandleCallbackParam handleCallbackParam);

    int size();

    static Builder builder(){
        return new Builder();
    }

    class Builder{
        private TaskExecutor taskExecutor;
        public Builder withTaskExecutor(TaskExecutor taskExecutor){
            this.taskExecutor = taskExecutor;
            return this;
        }
        public TaskCallbackProcessor build(){
            return new TaskCallbackProcessorImpl(taskExecutor);
        }
    }
}

//The default implementation
class TaskCallbackProcessorImpl implements TaskCallbackProcessor{

    TaskCallbackProcessorImpl(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
        this.callBackQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void pushCallBack(HandleCallbackParam callback) {
        callBackQueue.add(callback);
        LOGGER.debug("Push callback request, logId:{}", callback.logId());
    }

    @Override
    public int size() {
        return callBackQueue.size();
    }

    @Override
    public void start() {
        if (stateRef.compareAndSet(State.INITIALIZED, State.STARTING)) {
            try {
                validate();
                doStart();
            }
            catch (Throwable throwable) {
                stateRef.set(State.STOPPED);
                throw throwable;
            }
            stateRef.set(State.STARTED);
        }
    }

    @Override
    public void stop(Duration timeoutDuration) {
        if (stateRef.compareAndSet(State.STARTED, State.STOPPING)) {
            stopFlag = true;
            // stop callback, interrupt and wait
            if (triggerCallbackThread != null) {    // support empty admin address
                triggerCallbackThread.interrupt();
                try {
                    triggerCallbackThread.join();
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            // stop retry, interrupt and wait
            if (triggerRetryCallbackThread != null) {
                triggerRetryCallbackThread.interrupt();
                try {
                    triggerRetryCallbackThread.join();
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


    private void doCallback(List<HandleCallbackParam> callbackParamList, boolean appendFailCallbackFile) {
        boolean callbackRet = false;
        // callback, will retry if error
        for (TaskAdmin adminBiz : taskExecutor.getTaskAdmins()) {
            try {
                TaskResponse callbackResult = adminBiz.callback(callbackParamList);
                if (callbackResult != null && TaskResponse.SUCCESS.code() == callbackResult.code()) {
                    callbackLog(callbackParamList, "<br>----------- xxl-job job callback finish.");
                    callbackRet = true;
                    break;
                } else {
                    callbackLog(callbackParamList, "<br>----------- xxl-job job callback fail, callbackResult:" + callbackResult);
                }
            } catch (Exception e) {
                callbackLog(callbackParamList, "<br>----------- xxl-job job callback error, errorMsg:" + e.getMessage());
            }
        }
        if (!callbackRet && appendFailCallbackFile) {
            appendFailCallbackFile(callbackParamList);
        }
    }

    private void callbackLog(List<HandleCallbackParam> callbackParamList, String logContent) {
        for (HandleCallbackParam callbackParam : callbackParamList) {
            Path taskLogFilePath = TaskExecutorHelper.taskLogFilePath(
                taskExecutor.getContext().getLogRootPath()
                , callbackParam.taskId()
                , callbackParam.taskName()
                , LocalDate.now()
                , callbackParam.logId()
            );
            TaskHelper.log(taskLogFilePath, logContent);
        }
    }

    private Path getFailCallbackFilepath() {
        Path failCallbackFileDir = this.taskExecutor.getContext().getFailCallbackFilePath();
        return failCallbackFileDir.resolve(
            String.format("fail-callback-%s-%s.json", LocalDateUtils.format(LocalDateTime.now(), UNSIGNED_TIMESTAMP_PATTERN)));
    }

    private void appendFailCallbackFile(List<HandleCallbackParam> callbackParamList) {
        if (callbackParamList == null || callbackParamList.size() == 0) {
            return;
        }
        String jsonStr = JacksonUtil.toJson(callbackParamList);
        try {
            Files.writeString(getFailCallbackFilepath(), jsonStr, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("Fail to persist the callback list, please handler manually.", e);
            LOGGER.info("-----------------------------------------------------------------");
            LOGGER.info("{}", JacksonUtil.toJson(callbackParamList));
            LOGGER.info("-----------------------------------------------------------------");
        }
    }

    private void retryFailCallbackFile() {
        Path failCallbackFileDir = this.taskExecutor.getContext().getFailCallbackFilePath();
        if (!Files.exists(failCallbackFileDir)) {
            return;
        }
        Supplier<Stream<Path>> failCallbackFileStreamSupplier = () -> {
            try {
                return Files.list(failCallbackFileDir).filter(p -> {
                    String fileName = p.getFileName().toString();
                    return fileName.startsWith("fail-callback-") && fileName.endsWith(".json");
                });
            }catch (IOException e) {
                throw new TaskExecuteException("File list failure.", e);
            }
        };
        long count = failCallbackFileStreamSupplier.get().count();
        int maxRetryTimes = 10;
        int retryTimes = 0;
        while (count > 0) {
            failCallbackFileStreamSupplier.get().forEach(p -> {
                try {
                    String content = Files.readString(p, StandardCharsets.UTF_8);
                    Long fileSize = Files.size(p);
                    if (fileSize == 0) {
                        Files.deleteIfExists(p);
                    }
                    List<HandleCallbackParam> callbackParamList = JacksonUtil.fromJson(content, new TypeReference<>() {
                    });
                    doCallback(callbackParamList, false);
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    LOGGER.error("Fail to read the callback list.", e);
                    throw new TaskExecuteException("Read callback file failed", e);
                }
            });
            count = failCallbackFileStreamSupplier.get().count();
            if (count > 0) {
                ThreadUtils.sleepQuietly(Duration.ofSeconds(10));
                retryTimes++;
                LOGGER.info("Retry [{}/{}].", maxRetryTimes, count);
                if (retryTimes > maxRetryTimes) {
                    throw new TaskExecuteException("Retry exhaust.");
                }
            }
        }
    }

    private void validate(){
        if (CollectionUtil.isEmpty(taskExecutor.getTaskAdmins()))  {
            LOGGER.warn("Executor callback processor init fail, adminAddresses are empty.");
            throw new IllegalArgumentException("AdminAddresses is empty.");
        }
    }

    private void doStart(){
        // callback
        triggerCallbackThread = new Thread(() -> {

            // normal callback
            while (!stopFlag) {
                try {
                    HandleCallbackParam callback = this.callBackQueue.take();
                    if (callback != null) {
                        // callback list param
                        List<HandleCallbackParam> callbackParamList = new ArrayList<>();
                        callbackParamList.add(callback);
                        // callback, will retry if error
                        if (callbackParamList != null && callbackParamList.size() > 0) {
                            doCallback(callbackParamList, true);
                        }
                    }
                } catch (Exception e) {
                    if (!stopFlag) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }

            // last callback
            try {
                List<HandleCallbackParam> callbackParamList = new ArrayList<>();
                callBackQueue.drainTo(callbackParamList);
                if (callbackParamList != null && callbackParamList.size() > 0) {
                    doCallback(callbackParamList, true);
                }
            } catch (Exception e) {
                if (!stopFlag) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            LOGGER.info(">>>>>>>>>>> xxl-job, executor callback thread destroy.");

        });
        triggerCallbackThread.setDaemon(true);
        triggerCallbackThread.setName("TaskCallbackProcessor");
        triggerCallbackThread.start();

        // retry
        triggerRetryCallbackThread = new Thread(() -> {
            while (!stopFlag) {
                try {
                    retryFailCallbackFile();
                } catch (Exception e) {
                    if (!stopFlag) {
                        LOGGER.error(e.getMessage(), e);
                    }

                }
                try {
                    TimeUnit.SECONDS.sleep(Constants.BEAT_TIMEOUT_MILLISECONDS);
                } catch (InterruptedException e) {
                    if (!stopFlag) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
            LOGGER.info(">>>>>>>>>>> xxl-job, executor retry callback thread destroy.");
        });
        triggerRetryCallbackThread.setDaemon(true);
        triggerRetryCallbackThread.start();
    }

    private final TaskExecutor taskExecutor;

    private final LinkedBlockingQueue<HandleCallbackParam>  callBackQueue;
    private Thread triggerCallbackThread;
    private Thread triggerRetryCallbackThread;
    //Flag to stop the thread
    private volatile boolean stopFlag = false;

    private final AtomicReference<State> stateRef = new AtomicReference<>(State.INITIALIZED);

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCallbackProcessorImpl.class);
}
