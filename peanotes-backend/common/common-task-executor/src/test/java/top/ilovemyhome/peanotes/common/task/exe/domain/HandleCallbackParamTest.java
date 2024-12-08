package top.ilovemyhome.peanotes.common.task.exe.domain;

import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class HandleCallbackParamTest {

    @Test
    public void testConstruct() {
        HandleCallbackParam p1 = new HandleCallbackParam(
            100L,"foo", 1001L , LocalDateTime.of(2024, 11, 12, 8, 8, 8)
            , 200, "OK"
        );
        assertThat(p1.taskId()).isEqualTo(100L);
        assertThat(p1.taskName()).isEqualTo("foo");
        assertThat(p1.logId()).isEqualTo(1001L);
        assertThat(p1.logDateTime()).isEqualTo(LocalDateTime.of(2024, 11, 12, 8, 8, 8));
        assertThat(p1.handleCode()).isEqualTo(200);
        assertThat(p1.handleMsg()).isEqualTo("OK");
    }

    @Test
    public void testJsonSerialize() {
        HandleCallbackParam p1 = new HandleCallbackParam(
            100L,"foo", 1001L , LocalDateTime.of(2024, 11, 12, 8, 8, 8)
            , 200, "OK"
        );
        String jsonStr = JacksonUtil.toJson(p1);
        System.out.println(jsonStr);
        assertThat(jsonStr).isEqualToIgnoringWhitespace("""
            {
              "taskId" : 100,
              "taskName" : "foo",
              "logId" : 1001,
              "logDateTime" : "2024-11-12 08:08:08.000",
              "handleCode" : 200,
              "handleMsg" : "OK"
            }
            """);
        HandleCallbackParam p2 = JacksonUtil.fromJson(jsonStr, HandleCallbackParam.class);
        assertThat(p2).isEqualTo(p1);
        assertThat(p1).isEqualTo(p2);
    }
}
