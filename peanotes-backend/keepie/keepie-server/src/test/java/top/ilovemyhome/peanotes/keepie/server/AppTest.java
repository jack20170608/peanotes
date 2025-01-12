package top.ilovemyhome.peanotes.keepie.server;

import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AppTest {

    @Test
    public void test() {
        assertThat(true).isTrue();
    }

    @Test
    public void testThrowableToString(){
        Throwable t = new SQLException("Some mocked SQLException");
        System.out.println(t);

        try {
            Connection con = DriverManager.getConnection("foo");
        }catch (Throwable t1){
            System.out.println(t1);
        }
    }

    @Test
    public void testPrintWriter(){
        PrintWriter pw = new PrintWriter(System.out);
        pw.write("Hello World");
    }
}
