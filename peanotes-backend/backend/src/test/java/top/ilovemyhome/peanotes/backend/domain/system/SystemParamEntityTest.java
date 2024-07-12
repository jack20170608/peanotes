package top.ilovemyhome.peanotes.backend.domain.system;

import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class SystemParamEntityTest {

    @Test
    public void testToJson(){
        SystemParamEntity paramEntity = SystemParamEntity.builder()
            .withId(1L)
            .withParamName("app_name")
            .withParamValue("peanote")
            .withParamDesc("The description of the app")
            .withCreateDt(LocalDateTime.of(2024,6,1,12,18,10,333000000))
            .withUpdateDt(LocalDateTime.of(2024,6,1,13,28,32,999000000))
            .build();
        assertThat(JacksonUtil.fromJson(JacksonUtil.toJson(paramEntity), SystemParamEntity.class)).isEqualTo(paramEntity);
    }
}
