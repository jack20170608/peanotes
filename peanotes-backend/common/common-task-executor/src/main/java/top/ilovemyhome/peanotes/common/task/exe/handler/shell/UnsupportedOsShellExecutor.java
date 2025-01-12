package top.ilovemyhome.peanotes.common.task.exe.handler.shell;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

final class UnsupportedOsShellExecutor implements ShellExecutor {
    private final String osName;

    UnsupportedOsShellExecutor(String osName) {
        this.osName = osName;
    }

    @Override
    public ExecutionResult execute(String command) {
        throw new UnsupportedOperationException("Not supported on OS " + this.osName);
    }

    @Override
    public ExecutionResult execute(Path script, Map<String, String> arguments) {
        throw new UnsupportedOperationException("Not supported on OS " + this.osName);
    }

    @Override
    public ExecutionResult execute(InputStream script, Map<String, String> arguments) {
        throw new UnsupportedOperationException("Not supported on OS " + this.osName);
    }

    @Override
    public ExecutionResult execute(Path script, List<String> arguments) {
        throw new UnsupportedOperationException("Not supported on OS " + this.osName);
    }

    @Override
    public ExecutionResult execute(InputStream script, List<String> arguments) {
        throw new UnsupportedOperationException("Not supported on OS " + this.osName);
    }
}
