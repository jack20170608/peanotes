package top.ilovemyhome.peanotes.common.task.exe.helper;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Disabled
public class RuntimeTest {


    @Test
    public void test() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("java -version");
            int exitVal = process.waitFor();
            System.out.println("process exit value is " + exitVal);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testCmdInAndOut(){
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("powershell java -version");
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            System.out.println("OUTPUT");
            while ((line = stdoutReader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("ERROR");
            while ((line = stderrReader.readLine()) != null) {
                System.out.println(line);
            }
            int exitVal = process.waitFor();
            System.out.println("process exit value is " + exitVal);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
