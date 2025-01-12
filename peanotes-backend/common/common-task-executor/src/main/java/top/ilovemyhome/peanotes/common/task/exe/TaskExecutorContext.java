package top.ilovemyhome.peanotes.common.task.exe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.exe.domain.ITask;
import top.ilovemyhome.peanotes.common.task.exe.handler.MethodTaskHandler;
import top.ilovemyhome.peanotes.common.task.exe.handler.TaskHandler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static top.ilovemyhome.peanotes.backend.common.utils.NetUtil.findAvailablePort;

public interface TaskExecutorContext extends WebContext {

    String DEFAULT_APP_NAME = "DefaultTaskExecutor";
    int DEFAULT_PORT = 12580;
    String DEFAULT_CONTEXT_PATH = "/task";
    String DEFAULT_FQDN = "localhost";

    static DefaultBuilder builder() {
        return new DefaultBuilder();
    }

    String getAppName();

    void setTaskExecutor(TaskExecutor taskExecutor);
    TaskExecutor getTaskExecutor();

    boolean isCreateMuServer();

    TaskHandler getTaskHandler(String name);

    List<String> getListOfAdmin();

    Path getScriptSourcePath();

    Path getLogRootPath();

    Path getFailCallbackFilePath();

    //user's home directory
    Path DEFAULT_ROOT_PATH = Paths.get("").toAbsolutePath().resolve("task-executor");

    class DefaultBuilder {
        private String appName;

        //Need to build the server
        private boolean createMuServer = false;
        private String fqdn = DEFAULT_FQDN;
        private boolean sslEnabled = false;
        private String keystorePath;
        private String keystorePassword;
        private String keyPassword;
        private int port = DEFAULT_PORT;
        private String contextPath = DEFAULT_CONTEXT_PATH;

        //The uri that registered to the admin server
        private URI uri;
        private List<String> listOfAdmin;
        private List<Object> handlerBeans;

        private Path rootPath;

        private Path logRootPath;
        private Path scriptSourcePath;
        private Path failCallbackFilePath;

        private DefaultBuilder() {
        }

        public DefaultBuilder withAppName(String appName) {
            this.appName = appName;
            return this;
        }

        public DefaultBuilder withCreateMuServer(boolean createMuServer) {
            this.createMuServer = createMuServer;
            return this;
        }

        public DefaultBuilder withFqdn(String fqdn) {
            this.fqdn = fqdn;
            return this;
        }

        public DefaultBuilder withSslEnabled(boolean sslEnabled) {
            this.sslEnabled = sslEnabled;
            return this;
        }

        public DefaultBuilder withKeystorePath(String keystorePath) {
            this.keystorePath = keystorePath;
            return this;
        }

        public DefaultBuilder withKeystorePassword(String keystorePassword) {
            this.keystorePassword = keystorePassword;
            return this;
        }

        public DefaultBuilder withKeyPassword(String keyPassword) {
            this.keyPassword = keyPassword;
            return this;
        }

        public DefaultBuilder withPort(int port) {
            this.port = port;
            return this;
        }

        public DefaultBuilder withContextPath(String contextPath) {
            if (!StringUtils.startsWith(contextPath, "/")){
                this.contextPath = "/" + contextPath;
            }else {
                this.contextPath = contextPath;
            }
            return this;
        }

        public DefaultBuilder withUri(URI uri) {
            this.uri = uri;
            return this;
        }

        public DefaultBuilder withListOfAdmin(List<String> listOfAdmin) {
            this.listOfAdmin = ImmutableList.copyOf(listOfAdmin);
            return this;
        }

        public DefaultBuilder withHandlerBeans(List<Object> handlerBeans) {
            this.handlerBeans = handlerBeans;
            return this;
        }

        public DefaultBuilder withRootPath(Path rootPath) {
            this.rootPath = rootPath;
            return this;
        }

        public DefaultBuilder withLogRootPath(Path logRootPath) {
            this.logRootPath = logRootPath;
            return this;
        }

        public DefaultBuilder withScriptSourcePath(Path scriptSourcePath) {
            this.scriptSourcePath = scriptSourcePath;
            return this;
        }

        public DefaultBuilder withFailCallbackFilePath(Path failCallbackFilePath) {
            this.failCallbackFilePath = failCallbackFilePath;
            return this;
        }

        public TaskExecutorContextImpl build() {
            if (Objects.isNull(uri)) {
                this.uri = genUri();
            }
            return new TaskExecutorContextImpl(this.appName, createMuServer
                , fqdn, sslEnabled, keystorePath, keystorePassword, keyPassword
                , port, contextPath, this.uri, listOfAdmin, handlerBeans, rootPath
                , logRootPath, scriptSourcePath, failCallbackFilePath);
        }

        private URI genUri() {
            String schema = this.sslEnabled ? "https" : "http";
            if (StringUtils.isBlank(contextPath)) {
                this.contextPath = DEFAULT_CONTEXT_PATH;
            }
            if (Objects.isNull(appName) || appName.isEmpty()) {
                this.appName = DEFAULT_APP_NAME;
            }
            if (Objects.isNull(fqdn) || fqdn.isEmpty()) {
                try {
                    this.fqdn = InetAddress.getLocalHost().getCanonicalHostName();
                } catch (UnknownHostException e) {
                    throw new RuntimeException("Can't get canonical host name", e);
                }
            }
            if (port == 0) {
                this.port = findAvailablePort(DEFAULT_PORT);
            }
            try {
                return new URI(schema, null, fqdn, port, contextPath, null, null);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


class TaskExecutorContextImpl implements TaskExecutorContext {
    private final String appName;
    private final boolean createMuServer;
    private final String fqdn;
    private final boolean sslEnabled;
    private final String keystorePath;
    private final String keystorePassword;
    private final String keyPassword;
    private final String contextPath;
    private final int port;
    private final URI uri;
    private final List<String> listOfAdmin;
    private final List<Object> handlerBeans;
    private final Path rootPath;
    private final Path logRootPath;
    private final Path scriptSourcePath;
    private final Path failCallbackFilePath;

    private Map<String, TaskHandler> taskHandlerRepository = new ConcurrentHashMap<>();
    private transient TaskExecutor taskExecutor;

    TaskExecutorContextImpl(String appName
        , boolean createMuServer
        , String fqdn
        , boolean sslEnabled
        , String keystorePath
        , String keystorePassword
        , String keyPassword
        , int port
        , String contextPath
        , URI uri
        , List<String> listOfAdmin
        , List<Object> handlerBeans
        , Path rootPath
        , Path logRootPath
        , Path scriptSourcePath
        , Path failCallbackFilePath) {
        LOGGER.info("Initialize the executor context.");
        if (StringUtils.isBlank(appName)) {
            throw new IllegalArgumentException("AppName is empty.");
        }
        //Need to create the web server inside, normally we don't
        this.createMuServer = createMuServer;
        this.fqdn = fqdn;
        this.sslEnabled = sslEnabled;
        this.keystorePath = keystorePath;
        this.keystorePassword = keystorePassword;
        this.keyPassword = keyPassword;
        this.port = port;
        this.contextPath = contextPath;
        this.appName = appName;

        if (Objects.isNull(uri)) {
            throw new IllegalArgumentException("Bad URI string provided.");
        }
        this.uri = uri;
        this.listOfAdmin = listOfAdmin;
        //The handlers beans is
        if (Objects.isNull(handlerBeans) || handlerBeans.isEmpty()) {
            LOGGER.warn("Not handler beans set up.");
            this.handlerBeans = List.of();
        } else {
            this.handlerBeans = ImmutableList.copyOf(handlerBeans);
        }
        if (Objects.isNull(rootPath)) {
            this.rootPath = DEFAULT_ROOT_PATH;
        } else {
            this.rootPath = rootPath;
        }
        if (Objects.isNull(logRootPath)) {
            this.logRootPath = this.rootPath.resolve("logs");
        } else {
            this.logRootPath = logRootPath;
        }
        if (Objects.isNull(scriptSourcePath)) {
            this.scriptSourcePath = this.rootPath.resolve("scripts");
        } else {
            this.scriptSourcePath = scriptSourcePath;
        }
        if (Objects.isNull(failCallbackFilePath)) {
            this.failCallbackFilePath = this.rootPath.resolve("failcallback");
        } else {
            this.failCallbackFilePath = failCallbackFilePath;
        }
        LOGGER.info("Create the running directory.");
        try {
            Files.createDirectories(this.rootPath);
            Files.createDirectories(this.logRootPath);
            Files.createDirectories(this.scriptSourcePath);
            Files.createDirectories(this.failCallbackFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initTaskHandlerRepository();
        LOGGER.info("Executor context is {}.", this);
    }

    @Override
    public String toString() {
        return "TaskExecutorContextImpl{" +
            "failCallbackFilePath=" + failCallbackFilePath +
            ", scriptSourcePath=" + scriptSourcePath +
            ", logRootPath=" + logRootPath +
            ", rootPath=" + rootPath +
            ", listOfAdmin=" + listOfAdmin +
            ", uri=" + uri +
            ", contextPath='" + contextPath + '\'' +
            ", keyPassword='" + keyPassword + '\'' +
            ", keystorePassword='" + keystorePassword + '\'' +
            ", keystorePath='" + keystorePath + '\'' +
            ", sslEnabled=" + sslEnabled +
            ", fqdn='" + fqdn + '\'' +
            ", createMuServer=" + createMuServer +
            ", appName='" + appName + '\'' +
            '}';
    }

    @Override
    public String getAppName() {
        return this.appName;
    }

    @Override
    public TaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }

    @Override
    public void setTaskExecutor(TaskExecutor taskExecutor){
        if (Objects.isNull(this.taskExecutor)) {
            this.taskExecutor = taskExecutor;
        }
    }

    @Override
    public boolean isCreateMuServer() {
        return createMuServer;
    }

    @Override
    public String getFqdn() {
        return fqdn;
    }

    @Override
    public boolean isSslEnabled() {
        return sslEnabled;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getKeystorePath() {
        return keystorePath;
    }

    @Override
    public String getKeystorePassword() {
        return keystorePassword;
    }

    @Override
    public String getKeyPassword() {
        return keyPassword;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    public Map<String, TaskHandler> getTaskHandlerRepository() {
        return ImmutableMap.copyOf(taskHandlerRepository);
    }

    public Path getRootPath() {
        return rootPath;
    }

    public List<Object> getHandlerBeans() {
        return ImmutableList.copyOf(handlerBeans);
    }

    @Override
    public TaskHandler getTaskHandler(String name) {
        return taskHandlerRepository.get(name);
    }

    @Override
    public List<String> getListOfAdmin() {
        return listOfAdmin;
    }

    @Override
    public Path getScriptSourcePath() {
        return this.scriptSourcePath;
    }

    @Override
    public Path getLogRootPath() {
        return logRootPath;
    }

    @Override
    public Path getFailCallbackFilePath() {
        return failCallbackFilePath;
    }


    private void initTaskHandlerRepository() {
        if (Objects.nonNull(this.handlerBeans)) {
            this.handlerBeans.stream().filter(bean -> bean.getClass().getDeclaredMethods().length > 0).forEach(bean -> {
                for (Method method : bean.getClass().getDeclaredMethods()) {
                    ITask iTask = method.getAnnotation(ITask.class);
                    if (iTask != null) {
                        registerJobHandler(iTask, bean, method);
                    }
                }
            });
        }
    }

    private void registerJobHandler(ITask iTask, Object bean, Method executeMethod) {
        String name = iTask.value();
        //make and simplify the variables since they'll be called several times later
        Class<?> clazz = bean.getClass();
        String methodName = executeMethod.getName();
        if (name.trim().isEmpty()) {
            throw new RuntimeException("xxl-job method-jobhandler name invalid, for[" + clazz + "#" + methodName + "] .");
        }
        if (Objects.nonNull(getTaskHandler(name))) {
            throw new RuntimeException("xxl-job jobhandler[" + name + "] naming conflicts.");
        }
        executeMethod.setAccessible(true);

        // init and destroy
        Method initMethod = null;
        Method destroyMethod = null;

        if (!iTask.init().trim().isEmpty()) {
            try {
                initMethod = clazz.getDeclaredMethod(iTask.init());
                initMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("xxl-job method-jobhandler initMethod invalid, for[" + clazz + "#" + methodName + "] .");
            }
        }
        if (!iTask.destroy().trim().isEmpty()) {
            try {
                destroyMethod = clazz.getDeclaredMethod(iTask.destroy());
                destroyMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("xxl-job method-jobhandler destroyMethod invalid, for[" + clazz + "#" + methodName + "] .");
            }
        }
        taskHandlerRepository.put(name, new MethodTaskHandler(bean, executeMethod, initMethod, destroyMethod));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutorContextImpl.class);
}
