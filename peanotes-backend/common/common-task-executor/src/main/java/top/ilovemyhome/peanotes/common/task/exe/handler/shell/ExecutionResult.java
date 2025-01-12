package top.ilovemyhome.peanotes.common.task.exe.handler.shell;


import java.io.Writer;
import java.nio.file.Path;

public interface ExecutionResult {

    boolean isSuccess();

    int getReturnCode();

    String getStandardOutput();

    String getErrorOutput();

    Path getStandardOutputFile();

    Path getErrorOutputFile();
}
