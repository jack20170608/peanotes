package top.ilovemyhome.peanotes.common.task.admin.core.handler.impl;


import top.ilovemyhome.peanotes.common.task.admin.core.common.ScriptUtil;
import top.ilovemyhome.peanotes.common.task.admin.core.glue.GlueTypeEnum;
import top.ilovemyhome.peanotes.common.task.admin.core.handler.IJobHandler;
import top.ilovemyhome.peanotes.common.task.admin.core.job.JobContext;
import top.ilovemyhome.peanotes.common.task.admin.core.job.JobHelper;
import top.ilovemyhome.peanotes.common.task.admin.core.log.JobFileAppender;

import java.io.File;
import java.time.LocalDateTime;

/**
 * Created by xuxueli on 17/4/27.
 */
public class ScriptJobHandler extends IJobHandler {

    private Long jobId;
    private LocalDateTime glueUpdatetime;
    private String gluesource;
    private GlueTypeEnum glueType;

    public ScriptJobHandler(Long jobId, LocalDateTime glueUpdatetime, String gluesource, GlueTypeEnum glueType){
        this.jobId = jobId;
        this.glueUpdatetime = glueUpdatetime;
        this.gluesource = gluesource;
        this.glueType = glueType;

        // clean old script file
        File glueSrcPath = new File(JobFileAppender.getGlueSrcPath());
        if (glueSrcPath.exists()) {
            File[] glueSrcFileList = glueSrcPath.listFiles();
            if (glueSrcFileList!=null && glueSrcFileList.length>0) {
                for (File glueSrcFileItem : glueSrcFileList) {
                    if (glueSrcFileItem.getName().startsWith(String.valueOf(jobId)+"_")) {
                        glueSrcFileItem.delete();
                    }
                }
            }
        }

    }

    public LocalDateTime getGlueUpdatetime() {
        return glueUpdatetime;
    }

    @Override
    public void execute() throws Exception {

        if (!glueType.isScript()) {
            JobHelper.handleFail("glueType["+ glueType +"] invalid.");
            return;
        }

        // cmd
        String cmd = glueType.getCmd();

        // make script file
        String scriptFileName = JobFileAppender.getGlueSrcPath()
                .concat(File.separator)
                .concat(String.valueOf(jobId))
                .concat("_")
                .concat(String.valueOf(glueUpdatetime))
                .concat(glueType.getSuffix());
        File scriptFile = new File(scriptFileName);
        if (!scriptFile.exists()) {
            ScriptUtil.markScriptFile(scriptFileName, gluesource);
        }

        // log file
        String logFileName = JobContext.getXxlJobContext().getJobLogFileName();

        // script params：0=param、1=分片序号、2=分片总数
        String[] scriptParams = new String[3];
        scriptParams[0] = JobHelper.getJobParam();
        scriptParams[1] = String.valueOf(JobContext.getXxlJobContext().getShardIndex());
        scriptParams[2] = String.valueOf(JobContext.getXxlJobContext().getShardTotal());

        // invoke
        JobHelper.log("----------- script file:"+ scriptFileName +" -----------");
        int exitValue = ScriptUtil.execToFile(cmd, scriptFileName, logFileName, scriptParams);

        if (exitValue == 0) {
            JobHelper.handleSuccess();
        } else {
            JobHelper.handleFail("script exit value("+exitValue+") is failed");
        }

    }

}
