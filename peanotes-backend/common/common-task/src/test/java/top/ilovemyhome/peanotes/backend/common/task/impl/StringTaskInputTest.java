package top.ilovemyhome.peanotes.backend.common.task.impl;

import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.common.task.persistent.TaskOrder;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class StringTaskInputTest {

    @Test
    public void testToJson(){
        TaskOrder taskOrder = TaskOrder.builder()
            .withId(1L)
            .withKey("ICBCMonthyPay_MONTHLY_202406_PAY")
            .withName("ICBCMonthyPay")
            .withOrderType(OrderType.Monthly)
            .withAttributes(Map.of("client_country", "CHINA"))
            .withCreateDt(LocalDateTime.of(2024,8,1,11,12,54))
            .withLastUpdateDt(LocalDateTime.of(2024,8,1,8,1,18))
            .build();
        System.out.println(taskOrder);
        assertThat(taskOrder.getKey()).isEqualTo("ICBCMonthyPay_MONTHLY_202406_PAY");
    }

    @Test
    public void testDeserialize(){
        String jsonPayload = """
            {
              "taskId" : 100,
              "input" : "t1Input",
              "attributes" : {
                "p1" : "v1",
                "p2" : "v2"
              }
            }
            """;
        StringTaskInput taskInput = JacksonUtil.fromJson(jsonPayload, StringTaskInput.class);
        assertThat(taskInput.getTaskId()).isEqualTo(100L);
        assertThat(taskInput.getInput()).isEqualTo("t1Input");
        assertThat(taskInput.getAttributes().get("p1")).isEqualTo("v1");
        assertThat(taskInput.getAttributes().get("p2")).isEqualTo("v2");
    }
}
