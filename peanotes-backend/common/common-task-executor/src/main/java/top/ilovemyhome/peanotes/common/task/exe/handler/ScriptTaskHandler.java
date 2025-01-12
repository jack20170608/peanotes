package top.ilovemyhome.peanotes.common.task.exe.handler;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.utils.FileHelper;
import top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils;
import top.ilovemyhome.peanotes.common.task.exe.TaskContext;
import top.ilovemyhome.peanotes.common.task.exe.TaskExecuteException;
import top.ilovemyhome.peanotes.common.task.exe.TaskExecutorContext;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.TaskType;
import top.ilovemyhome.peanotes.common.task.exe.helper.TaskHelper;
import top.ilovemyhome.peanotes.common.task.exe.processor.TaskProcessor;
import top.ilovemyhome.peanotes.common.task.exe.handler.shell.ExecutionResult;
import top.ilovemyhome.peanotes.common.task.exe.handler.shell.ShellExecutor;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.UNSIGNED_TIMESTAMP_PATTERN;
import static top.ilovemyhome.peanotes.common.task.exe.TaskExecutor.CONTEXT;

public class ScriptTaskHandler implements TaskHandler {

    //Runtime context
    private final TaskExecutorContext taskExecutorContext;

    private final Long jobId;
    private final LocalDateTime lastUpdateDt;
    private final String source;
    private final TaskType taskType;


    public ScriptTaskHandler(TaskExecutorContext context
        , Long jobId, LocalDateTime lastUpdateDt, String source, TaskType taskType) {
        this.taskExecutorContext = context;
        this.jobId = jobId;
        this.lastUpdateDt = lastUpdateDt;
        this.source = source;
        this.taskType = taskType;

        // clean old script file
        Path scriptSourcePath = taskExecutorContext.getScriptSourcePath();
        FileHelper.deleteWithPredicate(scriptSourcePath, fileName -> fileName.startsWith(String.format("%s_", jobId)));
    }

    public LocalDateTime getLastUpdateDt() {
        return lastUpdateDt;
    }

    @Override
    public void doHandle() {
        TaskContext taskContext = CONTEXT.get();
        Logger logger = CONTEXT.get().getLogger();
        try {
            if (Objects.isNull(taskContext)) {
                throw new IllegalStateException("TaskContext is null in the current working thread");
            }
            Path scriptSourcePath = taskExecutorContext.getScriptSourcePath();
            String fileName = String.format("%s_%s%s", jobId, LocalDateUtils.format(lastUpdateDt, UNSIGNED_TIMESTAMP_PATTERN)
                , taskType.getSuffix());
            Path scriptPath = scriptSourcePath.resolve(fileName);
            if (!Files.exists(scriptPath)) {
                Files.writeString(scriptPath, source, Charset.defaultCharset(), StandardOpenOption.CREATE);
            }
            // script params：0=param、1=分片序号、2=分片总数
            List<String> scriptParams = List.of(taskContext.getTaskParam()
                , String.valueOf(taskContext.getShardIndex())
                , String.valueOf(taskContext.getShardTotal())
            );
            // invoke
            Path logFilePath = taskContext.getTaskLogFilePath();
            logger.info("----------- script file:{} -----------", scriptPath.getFileName());
            ShellExecutor shellExecutor = ShellExecutor.instance(taskContext.getTaskTempFilePath(), logFilePath, logFilePath);
            ExecutionResult executionResult = shellExecutor.execute(scriptPath, scriptParams);
            int exitValue = executionResult.getReturnCode();
            if (exitValue == 0) {
                TaskHelper.handleSuccess();
            } else {
                TaskHelper.handleServerFail("script exit value(" + exitValue + ") is failed");
            }
        } catch (Throwable e) {
            logger.error("Task execution failure.", e);
            throw new TaskExecuteException("Task execution failure.", e);
        }
    }
}
