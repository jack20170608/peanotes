package top.ilovemyhome.peanotes.backend.common.task.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.common.task.SimpleTaskOrder;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SimpleTaskOrderTest {

    @Test
    public void testSimpleTaskOrderToJson() {
        SimpleTaskOrder task = SimpleTaskOrder.builder()
            .withId(1L)
            .withName("FOO")
            .withKey(null)
            .withOrderType(OrderType.Monthly)
            .withOtherKeys(Map.of("k1","v1","k2","v2"))
            .withParams(Map.of("p1","v1","p2","v2"))
            .withCreateDt(LocalDateTime.of(2024,1,4,8,1,32))
            .withLastUpdateDt(LocalDateTime.of(2024,11,9,8,32,32))
            .build();

        String jsonSerializedTask = JacksonUtil.toJson(task);
        SimpleTaskOrder taskFromJson = JacksonUtil.fromJson(jsonSerializedTask, SimpleTaskOrder.class);

        assert taskFromJson != null;
        assertThat(taskFromJson.getId()).isEqualTo(1L);
        assertThat(taskFromJson.getName()).isEqualTo("FOO");
        assertThat(taskFromJson.getKey()).isEqualTo("FOO_MONTHLY_v1_v2");
        assertThat(taskFromJson.getOrderType()).isEqualTo(OrderType.Monthly);
        assertThat(taskFromJson.getOrderType()).isEqualTo(OrderType.Monthly);
        assertThat(taskFromJson.getOtherKeys()).isEqualTo(Map.of("k1","v1","k2","v2"));
        assertThat(taskFromJson.getOtherKeysInJson()).contains("k1", "k2", "v1", "v2");
        assertThat(taskFromJson.getParams()).isEqualTo(Map.of("p1","v1","p2","v2"));
        assertThat(taskFromJson.getParamsInJson()).contains("p1","v1","p2","v2");
        assertThat(taskFromJson.getCreateDt()).isNotNull();
        assertThat(taskFromJson.getLastUpdateDt()).isNotNull();

    }

    @Test
    public void testSimpleTaskOrderToJson2() {
        SimpleTaskOrder task = SimpleTaskOrder.builder()
            .withId(1L)
            .withName("FOO")
            .withKey("key")
            .withOrderType(OrderType.Monthly)
            .build();

        String jsonSerializedTask = JacksonUtil.toJson(task);
        SimpleTaskOrder taskFromJson = JacksonUtil.fromJson(jsonSerializedTask, SimpleTaskOrder.class);

        assert taskFromJson != null;
        assertThat(taskFromJson.getId()).isEqualTo(1L);
        assertThat(taskFromJson.getName()).isEqualTo("FOO");
        assertThat(taskFromJson.getKey()).isEqualTo("key");
        assertThat(taskFromJson.getOrderType()).isEqualTo(OrderType.Monthly);
        assertThat(taskFromJson.getOtherKeys()).isNull();
        assertThat(taskFromJson.getOtherKeysInJson()).isNull();
        assertThat(taskFromJson.getParams()).isNull();
        assertThat(taskFromJson.getParamsInJson()).isNull();
        assertThat(taskFromJson.getCreateDt()).isNull();
        assertThat(taskFromJson.getLastUpdateDt()).isNull();

    }
}
