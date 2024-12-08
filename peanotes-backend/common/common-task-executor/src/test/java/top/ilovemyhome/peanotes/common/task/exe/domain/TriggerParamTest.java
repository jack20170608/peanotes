package top.ilovemyhome.peanotes.common.task.exe.domain;

import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.ExecutorBlockStrategyEnum;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TriggerParamTest {

    @Test
    public void testTriggerParamBuild(){
        TriggerParam t1 = TriggerParam.builder()
            .withJobId(10L)
            .withExecutorHandler("s1")
            .withExecutorParams("p1")
            .withExecutorBlockStrategy(ExecutorBlockStrategyEnum.DISCARD_LATER)
            .withExecutorTimeout(Duration.ofSeconds(10))
            .withLogId(1234L)
            .withLogDateTime(LocalDateTime.of(2024,11,12,14,36,38))
            .withTaskType(TaskType.SHELL)
            .withScriptSource("ping localhost")
            .withScriptUpdatetime(LocalDateTime.of(2024,10,1,8,8,18))
            .withBroadcastIndex(1)
            .withBroadcastTotal(8)
            .build();
        assertThat(t1.jobId()).isEqualTo(10L);
        assertThat(t1.executorHandler()).isEqualTo("s1");
        assertThat(t1.executorParams()).isEqualTo("p1");
        assertThat(t1.executorBlockStrategy()).isEqualTo(ExecutorBlockStrategyEnum.DISCARD_LATER);
        assertThat(t1.executorTimeout()).isEqualTo(Duration.ofSeconds(10));
        assertThat(t1.logId()).isEqualTo(1234L);
        assertThat(t1.logDateTime()).isEqualTo(LocalDateTime.of(2024,11,12,14,36,38));
        assertThat(t1.taskType()).isEqualTo(TaskType.SHELL);
        assertThat(t1.scriptSource()).isEqualTo("ping localhost");
        assertThat(t1.scriptUpdatetime()).isEqualTo(LocalDateTime.of(2024,10,1,8,8,18));
        assertThat(t1.broadcastIndex()).isEqualTo(1);
        assertThat(t1.broadcastTotal()).isEqualTo(8);
    }


    @Test
    public void testJsonSerialize(){
        TriggerParam t1 = TriggerParam.builder()
            .withJobId(10L)
            .withExecutorHandler("s1")
            .withExecutorParams("p1")
            .withExecutorBlockStrategy(ExecutorBlockStrategyEnum.DISCARD_LATER)
            .withExecutorTimeout(Duration.ofSeconds(10))
            .withLogId(1234L)
            .withLogDateTime(LocalDateTime.of(2024,11,12,14,36,38))
            .withTaskType(TaskType.SHELL)
            .withScriptSource("ping localhost")
            .withScriptUpdatetime(LocalDateTime.of(2024,10,1,8,8,18))
            .withBroadcastIndex(1)
            .withBroadcastTotal(8)
            .build();
        System.out.println(t1);
        String jsonStr = JacksonUtil.toJson(t1);
        System.out.println(jsonStr);
        TriggerParam t2 = JacksonUtil.fromJson(jsonStr, TriggerParam.class);
        assertThat(t2).isEqualTo(t1);
    }

}//:~)
