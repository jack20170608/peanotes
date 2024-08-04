package top.ilovemyhome.peanotes.backend.common.task;

import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;

import java.time.LocalDateTime;
import java.util.Set;

public class TaskRecordTest {

    @Test
    public void testTaskRecordJsonSerialization() {
        TaskRecord taskRecord = TaskRecord.builder()
            .withId(1L)
            .withOrderKey("FOO_DAILY_20240601")
            .withName("Foo Task")
            .withDescription("A foo task for unit test purpose only!")
            .withExecutionKey("top.ilovemyhome.peanotes.backend.common.task.impl.execution.ConditionExceptionalExecution")
            .withSuccessorIds(Set.of(2L, 3L))
            .withInput("task input")
            .withOutput("task output")
            .withAsync(true)
            .withDummy(true)
            .withCreateDt(LocalDateTime.of(2024,7,1,12,9,19))
            .withLastUpdateDt(LocalDateTime.of(2024,7,2,12,9,19))
            .withStatus(TaskStatus.SUCCESS)
            .withStartDt(LocalDateTime.of(2024,7,1,12,10,19))
            .withEndDt(LocalDateTime.of(2024,7,1,12,20,19))
            .withSuccess(true)
            .withFailReason(null)
            .build();

        System.out.println(JacksonUtil.toJson(taskRecord));
    }
}
