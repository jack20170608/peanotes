package top.ilovemyhome.peanotes.common.task.exe.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TaskExecutorHelperTest {

    @TempDir
    private static Path tempPath;

    @Test
    public void testTaskExecutorHelper() throws Exception{
        Path logFilePath = TaskExecutorHelper.taskLogFilePath(tempPath
        , 100L, "foo", LocalDate.of(2024,1,11), 1000L);
        assertThat(logFilePath.getFileName().toString()).isEqualTo("foo_1000.log");
    }
}
