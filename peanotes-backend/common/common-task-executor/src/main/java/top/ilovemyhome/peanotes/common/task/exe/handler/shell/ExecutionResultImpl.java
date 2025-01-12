package top.ilovemyhome.peanotes.common.task.exe.handler.shell;

import java.nio.file.Path;

final class ExecutionResultImpl implements ExecutionResult {

    private final int returnCode;
    private final String output;
    private final String error;

    private final Path outputFile;
    private final Path errorFile;

    ExecutionResultImpl(int returnCode, String result, String error) {
        this.returnCode = returnCode;
        this.output = result;
        this.error = error;
        this.outputFile = null;
        this.errorFile = null;
    }

    ExecutionResultImpl(int returnCode, Path outputFile, Path errorFile) {
        this.returnCode = returnCode;
        this.outputFile = outputFile;
        this.errorFile = errorFile;
        this.output = null;
        this.error = null;
    }

    @Override
    public boolean isSuccess() {
        return this.returnCode == 0;
    }

    @Override
    public String getStandardOutput() {
        return this.output;
    }

    @Override
    public int getReturnCode() {
        return this.returnCode;
    }

    @Override
    public String getErrorOutput() {
        return this.error;
    }

    @Override
    public Path getStandardOutputFile() {
        return outputFile;
    }

    @Override
    public Path getErrorOutputFile() {
        return errorFile;
    }

}
