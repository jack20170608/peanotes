package top.ilovemyhome.peanotes.common.task.exe.domain;

import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LogParamTest {

    @Test
    public void testCreate(){
        LogParam logParam = new LogParam(LocalDateTime.of(2000,11,23,9,9,9)
        , 1L,"FOO",100L, 9);
        System.out.println(logParam);
        assertThat(logParam.logDateTime()).isEqualTo(LocalDateTime.of(2000,11,23,9,9,9));
        assertThat(logParam.logId()).isEqualTo(100L);
        assertThat(logParam.fromLineNum()).isEqualTo(9);

        String jsonStr = JacksonUtil.toJson(logParam);
        System.out.println(jsonStr);
        assertThat(jsonStr).isEqualToIgnoringWhitespace("""
            {
              "logDateTime" : "2000-11-23 09:09:09.000",
              "taskId" : 1,
              "taskName" : "FOO",
              "logId" : 100,
              "fromLineNum" : 9
            }
            """);
        LogParam logParam2 = JacksonUtil.fromJson(jsonStr, LogParam.class);
        assertThat(logParam).isEqualTo(logParam2);
        assertThat(logParam2).isEqualTo(logParam);
    }
}
