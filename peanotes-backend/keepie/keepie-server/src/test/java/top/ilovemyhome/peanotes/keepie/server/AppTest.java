package top.ilovemyhome.peanotes.keepie.server;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AppTest {

    @Test
    public void test() {
        assertThat(true).isTrue();
    }

    @Test
    public void testFiles(){
        assertThat(Files.exists(Paths.get("c:\\"))).isTrue();
        assertThat(Files.exists(Paths.get("d:\\jack\\123"))).isTrue();
    }
}
