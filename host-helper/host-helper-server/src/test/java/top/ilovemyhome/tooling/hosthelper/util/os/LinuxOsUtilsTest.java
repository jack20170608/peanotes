package top.ilovemyhome.tooling.hosthelper.util.os;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static top.ilovemyhome.tooling.hosthelper.util.os.LinuxOsUtils.isValidLinuxPath;

public class LinuxOsUtilsTest {

    @Test
    public void testIsValidLinuxPath() {
        assertThat(isValidLinuxPath("/home/user/file.txt")).isTrue();
        assertThat(isValidLinuxPath("/home/user//file.txt")).isFalse();
        assertThat(isValidLinuxPath("/home/user/file*")).isTrue();
    }


}
