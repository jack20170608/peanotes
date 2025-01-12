package top.ilovemyhome.peanotes.common.task.exe;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import io.muserver.*;
import io.muserver.openapi.OpenAPIObjectBuilder;
import io.muserver.rest.CollectionParameterStrategy;
import io.muserver.rest.RestHandlerBuilder;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.exe.domain.*;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.ExecutorBlockStrategyEnum;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.TaskType;
import top.ilovemyhome.peanotes.common.task.exe.handler.ScriptTaskHandler;
import top.ilovemyhome.peanotes.common.task.exe.handler.TaskHandler;
import top.ilovemyhome.peanotes.common.task.exe.processor.RegistryProcessor;
import top.ilovemyhome.peanotes.common.task.exe.processor.TaskCallbackProcessor;
import top.ilovemyhome.peanotes.common.task.exe.processor.TaskProcessor;
import top.ilovemyhome.peanotes.common.task.exe.web.HealthResource;
import top.ilovemyhome.peanotes.common.task.exe.web.TaskResource;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static io.muserver.ContextHandlerBuilder.context;
import static io.muserver.openapi.ExternalDocumentationObjectBuilder.externalDocumentationObject;
import static io.muserver.openapi.InfoObjectBuilder.infoObject;

public interface TaskExecutor extends LifeCycle {

    //Should not use thread local to store the task status, should a map instead
    InheritableThreadLocal<TaskContext> CONTEXT = new InheritableThreadLocal<>();

    TaskExecutorContext getContext();

    //The 2 internal components
    TaskCallbackProcessor getCallbackProcessor();

    RegistryProcessor getRegistryProcessor();

    MuServer getMuServer();

    String beat();

    TaskResponse idleBeat(IdleBeatParam idleBeatParam);

    TaskResponse run(TriggerParam triggerParam);

    TaskResponse kill(KillParam killParam);

    TaskResponse log(LogParam logParam);

    TaskProcessor registTaskThread(Long jobId, TaskHandler handler, String removeOldReason);

    TaskProcessor removeTaskThread(Long jobId, String removeOldReason);

    TaskProcessor loadTaskThread(Long jobId);

    List<TaskAdmin> getTaskAdmins();

    boolean isRegistered();

    static TaskExecutor.Builder builder() {
        return new Builder();
    }

    class Builder {
        private TaskExecutorContext taskExecutorContext;
        private MuServer muServer;

        public Builder withTaskExecutorContext(TaskExecutorContext taskExecutorContext) {
            this.taskExecutorContext = taskExecutorContext;
            return this;
        }

        public Builder withMuServer(MuServer muServer) {
            this.muServer = muServer;
            return this;
        }

        public TaskExecutor build() {
            return new TaskExecutorImpl(taskExecutorContext, muServer);
        }
    }
}


class TaskExecutorImpl implements TaskExecutor {

    public TaskExecutorImpl(TaskExecutorContext taskExecutorContext, MuServer muServer) {
        LOGGER.info("Context is {}.", taskExecutorContext.toString());
        this.context = taskExecutorContext;
        taskAdmins = this.context.getListOfAdmin().stream().filter(a -> {
            return Objects.nonNull(a)
                && !a.isBlank();
        }).map(a -> {
            return TaskAdmin.builder()
                .withAdminServerUrl(a)
                .withAccessToken(null)
                .build();
        }).collect(Collectors.toList());

        this.registryProcessor = RegistryProcessor.builder()
            .withTaskExecutor(this)
            .build();

        this.taskCallbackProcessor = TaskCallbackProcessor.builder()
            .withTaskExecutor(this)
            .build();
        //Init the attached Mu Server
        if (Objects.nonNull(muServer)) {
            this.muServer = muServer;
        }
        //Make the context aware of the task executor
        this.context.setTaskExecutor(this);
    }

    private MuServer createAndStartMuServer() {
        long start = System.currentTimeMillis();
        LOGGER.info("Create the mu server.");
        HttpsConfigBuilder httpsConfigBuilder = null;
        MuServerBuilder builder = null;
        //the ssl
        if (this.context.isSslEnabled()) {
            httpsConfigBuilder = HttpsConfigBuilder.httpsConfig()
                .withKeystoreType("JKS")
                .withProtocols("TLSv1.2");
            if (this.context.getKeystorePath().startsWith("classpath:")) {
                httpsConfigBuilder.withKeystoreFromClasspath(this.context.getKeystorePath().replaceFirst("classpath:", ""));
            } else {
                httpsConfigBuilder.withKeystore(new File(this.context.getKeystorePath()));
            }
            if (StringUtils.isNotBlank(this.context.getKeystorePassword())) {
                httpsConfigBuilder.withKeystorePassword(this.context.getKeystorePassword());
            }
            if (StringUtils.isNotBlank(this.context.getKeyPassword())) {
                httpsConfigBuilder.withKeyPassword(this.context.getKeyPassword());
            }
            builder = MuServerBuilder.httpsServer()
                .withHttpsConfig(httpsConfigBuilder)
                .withHttpsPort(this.context.getPort());
        } else {
            builder = MuServerBuilder.httpServer()
                .withHttpPort(this.context.getPort());
        }
        builder.addResponseCompleteListener(info -> {
                MuRequest req = info.request();
                MuResponse resp = info.response();
                LOGGER.info("Response completed: success={}, remoteAddr={}, clientAddress={}, req={}, status={}, duration={}."
                    , info.completedSuccessfully(), req.remoteAddress(), req.clientIP(), req, resp.status(), info.duration());
            })
            .withIdleTimeout(10, TimeUnit.MINUTES)
            .withMaxRequestSize(300_000_000) //300MB
            .addHandler(Method.GET, "/health", new HealthResource())
            .addHandler(context(context.getContextPath())
                .addHandler(createRestHandler())
            );
        MuServer muServer = builder.start();
        LOGGER.info("Mu server started in {} ms", System.currentTimeMillis() - start);
        LOGGER.info("Mu server will start app at {}.", muServer.uri().resolve(context.getContextPath()));
        return muServer;
    }

    private RestHandlerBuilder createRestHandler() {
        TaskResource taskResource = new TaskResource(this.context);
        return RestHandlerBuilder
            .restHandler(taskResource)
            .addCustomReader(new JacksonJsonProvider())
            .addCustomWriter(new JacksonJsonProvider())
            .withCollectionParameterStrategy(CollectionParameterStrategy.NO_TRANSFORM)
            .withOpenApiHtmlUrl("/api.html")
            .withOpenApiJsonUrl("/openapi.json")
            .addExceptionMapper(ClientErrorException.class, e -> Response.status(Response.Status.BAD_REQUEST.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("message", e.getMessage()))
                .build())
            .addExceptionMapper(InternalServerErrorException.class, e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("message", e.getMessage()))
                .build())
            .addExceptionMapper(JsonMappingException.class, e -> Response.status(Response.Status.BAD_REQUEST.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("message", e.getMessage()))
                .build())
            .withOpenApiDocument(
                OpenAPIObjectBuilder.openAPIObject()
                    .withInfo(
                        infoObject()
                            .withTitle("User API document")
                            .withDescription("This is a sample API for the graphql sample")
                            .withVersion("1.0")
                            .build())
                    .withExternalDocs(
                        externalDocumentationObject()
                            .withDescription("Documentation docs")
                            .withUrl(URI.create("https//muserver.io/jaxrs"))
                            .build()
                    )
            );
    }

    @Override
    public MuServer getMuServer() {
        return muServer;
    }

    @Override
    public TaskExecutorContext getContext() {
        return this.context;
    }

    @Override
    public TaskCallbackProcessor getCallbackProcessor() {
        return this.taskCallbackProcessor;
    }

    @Override
    public RegistryProcessor getRegistryProcessor() {
        return this.registryProcessor;
    }

    @Override
    public String beat() {
        return "PONG";
    }

    @Override
    public TaskResponse idleBeat(IdleBeatParam idleBeatParam) {
        TaskProcessor jobThread = this.loadTaskThread(idleBeatParam.jobId());
        if (jobThread != null && jobThread.isRunningOrHasQueue()) {
            return TaskResponse.of(500, "job thread is running or has trigger queue.", null);
        }
        return TaskResponse.SUCCESS;
    }

    @Override
    public TaskResponse run(TriggerParam triggerParam) {
        TaskProcessor taskProcessor = this.loadTaskThread(triggerParam.jobId());
        TaskHandler taskHandler = taskProcessor != null ? taskProcessor.getTaskHandler() : null;
        TaskType taskType = triggerParam.taskType();
        String removeOldReason = null;
        switch (taskType) {
            case BEAN -> {
                TaskHandler newTaskHandler = context.getTaskHandler(triggerParam.executorHandler());
                if (taskProcessor != null && taskHandler != newTaskHandler) {
                    // change handler, need kill old thread
                    removeOldReason = "change task handler or glue type, and terminate the old thread.";
                    taskProcessor = null;
                    taskHandler = null;
                }
                if (taskHandler == null) {
                    taskHandler = newTaskHandler;
                    if (taskHandler == null) {
                        return new TaskResponse(404, "Task handler [" + triggerParam.executorHandler() + "] not found.", null);
                    }
                }
            }
            case SHELL, PYTHON, PHP, NODEJS, POWERSHELL -> {
                // valid old jobThread
                if (taskProcessor != null &&
                    !(taskProcessor.getTaskHandler() instanceof ScriptTaskHandler
                        && ((ScriptTaskHandler) taskProcessor.getTaskHandler()).getLastUpdateDt() == triggerParam.scriptUpdatetime())) {
                    // change script or gluesource updated, need kill old thread
                    removeOldReason = "change job source or glue type, and terminate the old job thread.";
                    taskProcessor = null;
                    taskHandler = null;
                }
                // valid handler
                if (taskHandler == null) {
                    taskHandler = new ScriptTaskHandler(this.context, triggerParam.jobId()
                        , triggerParam.scriptUpdatetime()
                        , triggerParam.scriptSource()
                        , triggerParam.taskType());
                }
            }
            case GLUE_GROOVY -> {
                throw new TaskExecuteException("Task type is null or unsupported task type.");
            }
            case null, default -> {
                throw new TaskExecuteException("Task type is null or unsupported task type.");
            }
        }

        // executor block strategy
        if (taskProcessor != null) {
            ExecutorBlockStrategyEnum blockStrategy = triggerParam.executorBlockStrategy();
            switch (blockStrategy) {
                case DISCARD_LATER -> {
                    if (taskProcessor.isRunningOrHasQueue()) {
                        return TaskResponse.of(400, "block strategy effect：" + ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle(), null);
                    }
                }
                case COVER_EARLY -> {
                    if (taskProcessor.isRunningOrHasQueue()) {
                        removeOldReason = "block strategy effect：" + ExecutorBlockStrategyEnum.COVER_EARLY.getTitle();
                        taskProcessor = null;
                    }
                }
            }
        }
        if (taskProcessor == null) {
            taskProcessor = this.registTaskThread(triggerParam.jobId(), taskHandler, removeOldReason);
        }
        // push data to queue
        taskProcessor.pushTriggerQueue(triggerParam);
        return TaskResponse.SUCCESS;
    }


    @Override
    public TaskResponse kill(KillParam killParam) {
        TaskProcessor jobThread = this.loadTaskThread(killParam.jobId());
        if (jobThread != null) {
            this.removeTaskThread(killParam.jobId(), "scheduling center kill job.");
            return TaskResponse.SUCCESS;
        }
        return TaskResponse.ofSuccess("job thread already killed.");
    }

    @Override
    public TaskResponse log(LogParam logParam) {
        return TaskResponse.SUCCESS;
    }


    private final static ConcurrentMap<Long, TaskProcessor> jobThreadRepository = new ConcurrentHashMap<>();

    @Override
    public TaskProcessor registTaskThread(Long jobId, TaskHandler
        handler, String removeOldReason) {
        TaskProcessor newTaskProcessor = TaskProcessor.builder()
            .withTaskExecutor(this)
            .withJobId(jobId)
            .withTaskHandler(handler)
            .build();

        newTaskProcessor.start();
        LOGGER.info("Register success, jobId:{}, handler:{}", jobId, handler);
        TaskProcessor oldTaskProcessor = jobThreadRepository.put(jobId, newTaskProcessor);
        if (oldTaskProcessor != null) {
            oldTaskProcessor.stop();
            oldTaskProcessor.interrupt();
        }
        return newTaskProcessor;
    }

    @Override
    public TaskProcessor removeTaskThread(Long jobId, String
        removeOldReason) {
        TaskProcessor oldTaskProcessor = jobThreadRepository.remove(jobId);
        if (oldTaskProcessor != null) {
            oldTaskProcessor.stop();
            oldTaskProcessor.interrupt();
            return oldTaskProcessor;
        }
        return null;
    }

    @Override
    public TaskProcessor loadTaskThread(Long jobId) {
        return jobThreadRepository.get(jobId);
    }

    @Override
    public List<TaskAdmin> getTaskAdmins() {
        return taskAdmins;
    }

    @Override
    public boolean isRegistered() {
        return this.registryProcessor.isRegistered();
    }

    private MuServer muServer;
    private final RegistryProcessor registryProcessor;
    private final TaskCallbackProcessor taskCallbackProcessor;
    private final TaskExecutorContext context;
    private final List<TaskAdmin> taskAdmins;


    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutorImpl.class);


    @Override
    public State getState() {
        return stateRef.get();
    }

    @Override
    public void start() {
        if (stateRef.compareAndSet(State.INITIALIZED, State.STARTING)) {
            try {
                this.taskCallbackProcessor.start();
                this.registryProcessor.start();
            } catch (Throwable throwable) {
                stateRef.set(State.STOPPED);
                throw throwable;
            }
            //Create and start the mu server
            if (Objects.isNull(this.muServer) && this.context.isCreateMuServer()){
                this.muServer = createAndStartMuServer();
            }
            stateRef.set(State.STARTED);
        }
    }


    @Override
    public void stop(Duration timeoutDuration) {
        if (stateRef.compareAndSet(State.STARTED, State.STOPPING)) {
            if (this.context.isCreateMuServer() && Objects.nonNull(this.muServer)){
                this.muServer.stop();
            }
            this.registryProcessor.stop();
            this.taskCallbackProcessor.stop();
            stateRef.set(State.STOPPED);
        }
    }

    private final AtomicReference<State> stateRef = new AtomicReference<>(State.INITIALIZED);

}
