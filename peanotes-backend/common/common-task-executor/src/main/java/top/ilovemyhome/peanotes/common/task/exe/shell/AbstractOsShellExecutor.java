package top.ilovemyhome.peanotes.common.task.exe.shell;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

abstract class AbstractOsShellExecutor implements ShellExecutor {

    protected final Path tempPath;
    protected final String executionCommand;
    protected final String paramQuote;
    protected final Path outputPath;
    protected final Path errorPath;

    AbstractOsShellExecutor(Path tempPath, String executionCommand, String paramQuote) {
        this(tempPath, executionCommand, paramQuote, null, null);
    }

    AbstractOsShellExecutor(Path tempPath, String executionCommand, String paramQuote, Path outputPath, Path errorPath) {
        this.tempPath = tempPath;
        this.executionCommand = executionCommand;
        this.paramQuote = paramQuote;
        if (tempPath != null && !Files.exists(tempPath)) {
            try {
                Files.createDirectories(tempPath);
            } catch (IOException e) {
                throw new UncheckedIOException("Cannot create given temp folder: " + tempPath.toAbsolutePath(), e);
            }
        }
        this.outputPath = outputPath;
        this.errorPath = errorPath;
    }

    protected ProcessBuilder createProcessBuilder(List<String> commandLine) {
        return new ProcessBuilder(commandLine);
    }

    protected ExecutionResult execute(ProcessBuilder processBuilder) {
        Writer outputWriter;
        Writer errorWriter;
        ExecutionResultImpl result;
        PrintWriter outputBuffer = null;
        PrintWriter errorBuffer = null;
        try {
            if (Objects.isNull(this.outputPath)) {
                outputWriter = new StringWriter();
            } else {
                outputWriter = new FileWriter(outputPath.toFile());
            }
            if (Objects.isNull(this.errorPath)) {
                errorWriter = new StringWriter();
            } else {
                errorWriter = new FileWriter(errorPath.toFile());
            }
            outputBuffer = new PrintWriter(outputWriter, true);
            errorBuffer = new PrintWriter(errorWriter, true);
            Process process = processBuilder.start();
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), outputBuffer::println);
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), errorBuffer::println);
            List<Thread> threads = Arrays.asList(
                new Thread(outputGobbler, JSE_GLOBBER_THREAD_NAME),
                new Thread(errorGobbler, JSE_GLOBBER_THREAD_NAME)
            );
            threads.forEach(thread -> {
                thread.setDaemon(true);
                thread.start();
            });
            for (StreamGobbler gobbler : Arrays.asList(outputGobbler, errorGobbler)) {
                gobbler.waitTillFinished();
            }
            if (errorWriter instanceof StringWriter && outputWriter instanceof StringWriter) {
                result = new ExecutionResultImpl(process.waitFor(), getString((StringWriter) outputWriter), getString((StringWriter) errorWriter));
            } else {
                result = new ExecutionResultImpl((process.waitFor()), outputPath, errorPath);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Wait for process termination interrupted", e);
        } catch (IOException e) {
            throw new UncheckedIOException("Shell cannot be executed", e);
        } finally {
            if (Objects.nonNull(outputBuffer)){
                outputBuffer.flush();
                outputBuffer.close();
            }
            if (Objects.nonNull(errorBuffer)){
                errorBuffer.close();
            }
        }
        return result;
    }

    private String getString(StringWriter outputStringWriter) {
        return outputStringWriter.getBuffer().toString().trim();
    }

    protected String readStream(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
    }

    //2. Execute from Stream
    @Override
    public ExecutionResult execute(InputStream script, Map<String, String> arguments) {
        try {
            String prefix = "java-shell-";
            String suffix = getSuffix();
            Path tempFile = this.tempPath != null ? Files.createTempFile(this.tempPath, prefix, suffix) : Files.createTempFile(prefix, suffix);
            try {
                Files.write(tempFile, readStream(script).getBytes(UTF_8), TRUNCATE_EXISTING);
                return execute(tempFile, arguments);
            } finally {
                Files.deleteIfExists(tempFile);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot handle script", e);
        }
    }

    @Override
    public ExecutionResult execute(InputStream script, List<String> arguments) {
        try {
            String prefix = "java-shell-";
            String suffix = getSuffix();
            Path tempFile = this.tempPath != null ? Files.createTempFile(this.tempPath, prefix, suffix) : Files.createTempFile(prefix, suffix);
            try {
                Files.write(tempFile, readStream(script).getBytes(UTF_8), TRUNCATE_EXISTING);
                return execute(tempFile, arguments);
            } finally {
                Files.deleteIfExists(tempFile);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot handle script", e);
        }
    }


    protected String getSuffix() {
        if (this instanceof WindowsPowerShellExecutor) {
            return ".ps1";
        } else if (this instanceof LinuxBashShellExecutor) {
            return ".bash";
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
