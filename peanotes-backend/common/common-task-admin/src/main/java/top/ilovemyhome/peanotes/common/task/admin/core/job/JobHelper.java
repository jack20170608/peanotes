package top.ilovemyhome.peanotes.common.task.admin.core.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import top.ilovemyhome.peanotes.backend.common.Constants;
import top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils;
import top.ilovemyhome.peanotes.common.task.admin.core.log.JobFileAppender;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

/**
 * helper for xxl-job
 *
 * @author xuxueli 2020-11-05
 */
public class JobHelper {

    // ---------------------- base info ----------------------

    /**
     * current JobId
     *
     * @return
     */
    public static long getJobId() {
        JobContext xxlJobContext = JobContext.getXxlJobContext();
        if (xxlJobContext == null) {
            return -1;
        }

        return xxlJobContext.getJobId();
    }

    /**
     * current JobParam
     *
     * @return
     */
    public static String getJobParam() {
        JobContext xxlJobContext = JobContext.getXxlJobContext();
        if (xxlJobContext == null) {
            return null;
        }

        return xxlJobContext.getJobParam();
    }

    // ---------------------- for log ----------------------

    /**
     * current JobLogFileName
     *
     * @return
     */
    public static String getJobLogFileName() {
        JobContext xxlJobContext = JobContext.getXxlJobContext();
        if (xxlJobContext == null) {
            return null;
        }

        return xxlJobContext.getJobLogFileName();
    }

    // ---------------------- for shard ----------------------

    /**
     * current ShardIndex
     *
     * @return
     */
    public static int getShardIndex() {
        JobContext xxlJobContext = JobContext.getXxlJobContext();
        if (xxlJobContext == null) {
            return -1;
        }

        return xxlJobContext.getShardIndex();
    }

    /**
     * current ShardTotal
     *
     * @return
     */
    public static int getShardTotal() {
        JobContext xxlJobContext = JobContext.getXxlJobContext();
        if (xxlJobContext == null) {
            return -1;
        }

        return xxlJobContext.getShardTotal();
    }

    // ---------------------- tool for log ----------------------

    private static Logger logger = LoggerFactory.getLogger("xxl-job logger");

    /**
     * append log with pattern
     *
     * @param appendLogPattern   like "aaa {} bbb {} ccc"
     * @param appendLogArguments like "111, true"
     */
    public static boolean log(String appendLogPattern, Object... appendLogArguments) {

        FormattingTuple ft = MessageFormatter.arrayFormat(appendLogPattern, appendLogArguments);
        String appendLog = ft.getMessage();

        /*appendLog = appendLogPattern;
        if (appendLogArguments!=null && appendLogArguments.length>0) {
            appendLog = MessageFormat.format(appendLogPattern, appendLogArguments);
        }*/

        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        return logDetail(callInfo, appendLog);
    }

    /**
     * append exception stack
     *
     * @param e
     */
    public static boolean log(Throwable e) {

        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String appendLog = stringWriter.toString();

        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        return logDetail(callInfo, appendLog);
    }

    /**
     * append log
     *
     * @param callInfo
     * @param appendLog
     */
    private static boolean logDetail(StackTraceElement callInfo, String appendLog) {
        JobContext xxlJobContext = JobContext.getXxlJobContext();
        if (xxlJobContext == null) {
            return false;
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(LocalDateUtils.format(LocalDateTime.now(), Constants.JSON_DATETIME_FORMAT)).append(" ")
            .append("[" + callInfo.getClassName() + "#" + callInfo.getMethodName() + "]").append("-")
            .append("[" + callInfo.getLineNumber() + "]").append("-")
            .append("[" + Thread.currentThread().getName() + "]").append(" ")
            .append(appendLog != null ? appendLog : "");
        String formatAppendLog = stringBuffer.toString();

        // appendlog
        String logFileName = xxlJobContext.getJobLogFileName();

        if (logFileName != null && logFileName.trim().length() > 0) {
            JobFileAppender.appendLog(logFileName, formatAppendLog);
            return true;
        } else {
            logger.info(">>>>>>>>>>> {}", formatAppendLog);
            return false;
        }
    }

    // ---------------------- tool for handleResult ----------------------

    /**
     * handle success
     *
     * @return
     */
    public static boolean handleSuccess() {
        return handleResult(JobContext.HANDLE_CODE_SUCCESS, null);
    }

    /**
     * handle success with log msg
     *
     * @param handleMsg
     * @return
     */
    public static boolean handleSuccess(String handleMsg) {
        return handleResult(JobContext.HANDLE_CODE_SUCCESS, handleMsg);
    }

    /**
     * handle fail
     *
     * @return
     */
    public static boolean handleFail() {
        return handleResult(JobContext.HANDLE_CODE_FAIL, null);
    }

    /**
     * handle fail with log msg
     *
     * @param handleMsg
     * @return
     */
    public static boolean handleFail(String handleMsg) {
        return handleResult(JobContext.HANDLE_CODE_FAIL, handleMsg);
    }

    /**
     * handle timeout
     *
     * @return
     */
    public static boolean handleTimeout() {
        return handleResult(JobContext.HANDLE_CODE_TIMEOUT, null);
    }

    /**
     * handle timeout with log msg
     *
     * @param handleMsg
     * @return
     */
    public static boolean handleTimeout(String handleMsg) {
        return handleResult(JobContext.HANDLE_CODE_TIMEOUT, handleMsg);
    }

    /**
     * @param handleCode 200 : success
     *                   500 : fail
     *                   502 : timeout
     * @param handleMsg
     * @return
     */
    public static boolean handleResult(int handleCode, String handleMsg) {
        JobContext xxlJobContext = JobContext.getXxlJobContext();
        if (xxlJobContext == null) {
            return false;
        }

        xxlJobContext.setHandleCode(handleCode);
        if (handleMsg != null) {
            xxlJobContext.setHandleMsg(handleMsg);
        }
        return true;
    }


}
