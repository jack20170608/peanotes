package top.ilovemyhome.peanotes.common.task.exe.helper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ScriptTaskHelper {

    public static int execToFile(String command, Path scriptFilePath, Path logFilePath, String... params) {

        FileOutputStream fileOutputStream = null;
        Thread inputThread = null;
        Thread errThread = null;
        try {
            // file
            fileOutputStream = new FileOutputStream(logFilePath.toFile(), true);

            // command
            List<String> cmdarray = new ArrayList<>();
            cmdarray.add(command);
            cmdarray.add(scriptFilePath.toFile().getAbsolutePath());

            if (params != null && params.length > 0) {
                for (String param : params) {
                    cmdarray.add(param);
                }
            }
            String[] cmdarrayFinal = cmdarray.toArray(new String[cmdarray.size()]);

            // process-exec
            final Process process = Runtime.getRuntime().exec(cmdarrayFinal);

            // log-thread
            final FileOutputStream finalFileOutputStream = fileOutputStream;
            inputThread = new Thread(() -> {
                try {
                    copy(process.getInputStream(), finalFileOutputStream, new byte[1024]);
                } catch (IOException e) {
                    TaskHelper.log(logFilePath, e);
                }
            });
            errThread = new Thread(() -> {
                try {
                    copy(process.getErrorStream(), finalFileOutputStream, new byte[1024]);
                } catch (IOException e) {
                    TaskHelper.log(logFilePath, e);
                }
            });
            inputThread.start();
            errThread.start();

            // process-wait
            int exitValue = process.waitFor();      // exit code: 0=success, 1=error

            // log-thread join
            inputThread.join();
            errThread.join();

            return exitValue;
        } catch (Exception e) {
            TaskHelper.log(logFilePath, e);
            return -1;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    TaskHelper.log(logFilePath, e);
                }
            }
            if (inputThread != null && inputThread.isAlive()) {
                inputThread.interrupt();
            }
            if (errThread != null && errThread.isAlive()) {
                errThread.interrupt();
            }
        }
    }


    private static long copy(InputStream inputStream, OutputStream outputStream, byte[] buffer) throws IOException {
        try {
            long total = 0;
            for (;;) {
                int res = inputStream.read(buffer);
                if (res == -1) {
                    break;
                }
                if (res > 0) {
                    total += res;
                    if (outputStream != null) {
                        outputStream.write(buffer, 0, res);
                    }
                }
            }
            outputStream.flush();
            //out = null;
            inputStream.close();
            inputStream = null;
            return total;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

}
