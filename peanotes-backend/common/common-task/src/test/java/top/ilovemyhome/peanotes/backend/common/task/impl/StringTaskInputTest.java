package top.ilovemyhome.peanotes.backend.common.task.impl;

import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.common.task.SimpleTaskOrder;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class StringTaskInputTest {

    @Test
    public void testToJson(){
        SimpleTaskOrder taskOrder = SimpleTaskOrder.builder()
            .withId(1L)
            .withName("ICBCMonthyPay")
            .withOrderType(OrderType.Monthly)
            .withOtherKeys(Map.of("type", "PAY", "month", "202406"))
            .withParams(Map.of("client_country", "CHINA"))
            .withCreateDt(LocalDateTime.of(2024,8,1,11,12,54))
            .withLastUpdateDt(LocalDateTime.of(2024,8,1,8,1,18))
            .build();
        assertThat(taskOrder.getKey()).isEqualTo("ICBCMonthyPay_MONTHLY_202406_PAY".toUpperCase());
    }

    @Test
    public void testDeserialize(){
        String jsonPayload = """
            {
              "taskOrder" : {
                "id" : 5,
                "name" : "foo",
                "key" : "FOO_DAILY_20240718_ASYNC",
                "orderType" : "Daily",
                "otherKeys" : {
                  "bizDate" : "20240718",
                  "runType" : "ASYNC"
                },
                "params" : {
                  "type" : "fund"
                },
                "createDt" : "2024-08-04 13:29:48.416",
                "lastUpdateDt" : "2024-08-04 13:29:48.416"
              },
              "input" : "t1Input",
              "attributes" : {
                "p1" : "v1",
                "p2" : "v2"
              }
            }
            """;
        StringTaskInput taskInput = JacksonUtil.fromJson(jsonPayload, StringTaskInput.class);
        assertThat(taskInput.getTaskOrder().getKey()).isEqualTo("FOO_DAILY_20240718_ASYNC");
        assertThat(taskInput.getTaskOrder().getParams().get("type")).isEqualTo("fund");
        assertThat(taskInput.getTaskOrder().getParams().size()).isEqualTo(1);
        assertThat(taskInput.getInput()).isEqualTo("t1Input");
        assertThat(taskInput.getAttributes().get("p1")).isEqualTo("v1");
        assertThat(taskInput.getAttributes().get("p2")).isEqualTo("v2");
    }
}
