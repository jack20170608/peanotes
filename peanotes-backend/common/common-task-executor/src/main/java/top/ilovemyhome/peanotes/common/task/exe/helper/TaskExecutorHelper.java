package top.ilovemyhome.peanotes.common.task.exe.helper;

import top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils;
import top.ilovemyhome.peanotes.backend.common.utils.StringConvertUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;

import static top.ilovemyhome.peanotes.backend.common.utils.StringConvertUtils.toStr;

public final class TaskExecutorHelper {

    public static Path taskLogFilePath(Path logRootPath, Long taskId, String taskName,  LocalDate triggerDate, Long logId) {
        Objects.requireNonNull(logRootPath);
        Objects.requireNonNull(triggerDate);
        Objects.requireNonNull(taskName);
        Objects.requireNonNull(logId);
        try {
            String dateStr = LocalDateUtils.format(triggerDate, LocalDateUtils.DATE_PATTERN);
            Path taskPath = logRootPath.resolve(toStr(taskId)).resolve(dateStr);
            Files.createDirectories(taskPath);
            String fileName = taskName +"_" + toStr(logId) + ".log";
            return taskPath.resolve(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path taskTaskTempPath(Path rootPath, String taskName, Long logId) {
        Objects.requireNonNull(rootPath);
        Objects.requireNonNull(logId);
        Objects.requireNonNull(taskName);
        try {
            Path taskPath = rootPath.resolve(taskName);
            Path tempPath = taskPath.resolve(toStr(logId));
            Files.createDirectories(tempPath);
            return tempPath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
