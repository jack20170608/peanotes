package top.ilovemyhome.peanotes.common.task.exe.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils;
import top.ilovemyhome.peanotes.common.task.exe.TaskContext;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.HandlerStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Objects;

import static top.ilovemyhome.peanotes.common.task.exe.TaskExecutor.CONTEXT;


public class TaskHelper {

//    public static boolean log(Path logFilePath, String appendLogPattern, Object... appendLogArguments) {
//        FormattingTuple ft = MessageFormatter.arrayFormat(appendLogPattern, appendLogArguments);
//        String appendLog = ft.getMessage();
//        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
//        return logDetail(logFilePath, callInfo, appendLog);
//    }
//
//    public static boolean log(Path logFilePath, Throwable e) {
//        StringWriter stringWriter = new StringWriter();
//        e.printStackTrace(new PrintWriter(stringWriter));
//        String appendLog = stringWriter.toString();
//
//        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
//        return logDetail(logFilePath, callInfo, appendLog);
//    }

    private static boolean logDetail(Path logFilePath, StackTraceElement callInfo, String appendLog) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(LocalDateUtils.format(LocalDateTime.now(), LocalDateUtils.UNSIGNED_TIMESTAMP_PATTERN)).append(" ")
            .append("[" + callInfo.getClassName() + "#" + callInfo.getMethodName() + "]").append("-")
            .append("[" + callInfo.getLineNumber() + "]").append("-")
            .append("[" + Thread.currentThread().getName() + "]").append(" ")
            .append(appendLog != null ? appendLog : "");
        String formatAppendLog = stringBuffer.toString();
        if (Objects.nonNull(logFilePath)) {
            appendLog(logFilePath, formatAppendLog);
            return true;
        } else {
            LOGGER.info(">>>>>>>>>>> {}", formatAppendLog);
            return false;
        }
    }

    private static void appendLog(Path logFilePath, String appendLog) {
        // log file
        if (Objects.isNull(logFilePath)) {
            return;
        }
        if (Files.notExists(logFilePath)) {
            try {
                Files.createFile(logFilePath);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                return;
            }
        }
        try {
            Files.writeString(logFilePath, appendLog + "\r\n", StandardCharsets.UTF_8
                , StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static boolean handleSuccess(){
        return handleResult(HandlerStatus.OK, null);
    }

    public static boolean handleSuccess(String handleMsg) {
        return handleResult(HandlerStatus.OK, handleMsg);
    }


    public static boolean handleClientFail(){
        return handleResult(HandlerStatus.CLIENT_ERROR, null);
    }

    public static boolean handleClientFail(String handleMsg) {
        return handleResult(HandlerStatus.CLIENT_ERROR, handleMsg);
    }

    public static boolean handleServerFail(){
        return handleResult(HandlerStatus.SERVER_ERROR, null);
    }

    public static boolean handleServerFail(String handleMsg) {
        return handleResult(HandlerStatus.SERVER_ERROR, handleMsg);
    }

    public static boolean handleTimeout(String handleMsg){
        return handleResult(HandlerStatus.HANDLE_CODE_TIMEOUT, handleMsg);
    }

    public static boolean handleResult(HandlerStatus handlerStatus, String handleMsg) {
        TaskContext taskContext = CONTEXT.get();
        if (taskContext == null) {
            return false;
        }

        taskContext.setHandlerStatus(handlerStatus);
        if (handleMsg != null) {
            taskContext.setHandlerMessage(handleMsg);
        }
        return true;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskHelper.class);
}
