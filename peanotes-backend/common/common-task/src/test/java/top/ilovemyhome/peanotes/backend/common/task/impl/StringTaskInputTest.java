package top.ilovemyhome.peanotes.backend.common.task.impl;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class StringTaskInputTest {

    @Test
    public void testToJson(){
        SimpleTaskOrder taskOrder = new SimpleTaskOrder("ICBCMonthyPay"
            , OrderType.Monthly, Map.of("type", "pay", "month","202406"));
        StringTaskInput input = new StringTaskInput(
            taskOrder, "input", null
        );
        assertThat(taskOrder.getKey()).isEqualTo("ICBCMonthyPay_MONTHLY_202406_pay");
    }
}
