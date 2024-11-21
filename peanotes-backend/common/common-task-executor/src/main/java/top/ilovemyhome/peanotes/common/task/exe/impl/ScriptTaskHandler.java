package top.ilovemyhome.peanotes.common.task.exe.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.utils.FileHelper;
import top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils;
import top.ilovemyhome.peanotes.common.task.exe.TaskExecutorContext;
import top.ilovemyhome.peanotes.common.task.exe.TaskHandler;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.ScriptType;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.UNSIGNED_DATETIME_PATTERN;
import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.UNSIGNED_TIMESTAMP_PATTERN;

public class ScriptTaskHandler implements TaskHandler{

    private final TaskExecutorContext taskExecutorContext;

    private final Long jobId;
    private final LocalDateTime lastUpdateDt;
    private final String source;
    private final ScriptType scriptType;

    public ScriptTaskHandler(TaskExecutorContext context
        , Long jobId, LocalDateTime lastUpdateDt, String source, ScriptType scriptType){
        this.taskExecutorContext = context;
        this.jobId = jobId;
        this.lastUpdateDt = lastUpdateDt;
        this.source = source;
        this.scriptType = scriptType;

        // clean old script file
        Path scriptSourcePath = taskExecutorContext.getScriptSourcePath();
        FileHelper.deleteWithPredicate(scriptSourcePath, fileName -> fileName.startsWith(String.format("%s_", jobId)));
    }

    public LocalDateTime getLastUpdateDt() {
        return lastUpdateDt;
    }

    @Override
    public void handle() throws Exception {
        String cmd = scriptType.getCmd();

        // make script file
        Path scriptSourcePath = taskExecutorContext.getScriptSourcePath();
        String fileName = String.format("%s_%s%s", jobId, LocalDateUtils.format(lastUpdateDt, UNSIGNED_TIMESTAMP_PATTERN)
            , scriptType.getSuffix());
        Path scriptPath = scriptSourcePath.resolve(fileName);
        if (!Files.exists(scriptPath)) {
            Files.writeString(scriptPath, source, Charset.defaultCharset(), StandardOpenOption.CREATE);
        }

        // log file
        String logFileName = XxlJobContext.getXxlJobContext().getJobLogFileName();

        // script params：0=param、1=分片序号、2=分片总数
        String[] scriptParams = new String[3];
        scriptParams[0] = XxlJobHelper.getJobParam();
        scriptParams[1] = String.valueOf(XxlJobContext.getXxlJobContext().getShardIndex());
        scriptParams[2] = String.valueOf(XxlJobContext.getXxlJobContext().getShardTotal());

        // invoke
        XxlJobHelper.log("----------- script file:"+ scriptFileName +" -----------");
        int exitValue = ScriptUtil.execToFile(cmd, scriptFileName, logFileName, scriptParams);

        if (exitValue == 0) {
            XxlJobHelper.handleSuccess();
            return;
        } else {
            XxlJobHelper.handleFail("script exit value("+exitValue+") is failed");
            return ;
        }

    }

    private Path genFileName(){

    }


    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptTaskHandler.class);

}
