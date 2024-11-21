package top.ilovemyhome.peanotes.common.task.admin.web.dto;

import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JobInfoQueryDtoTest {

    @Test
    public void testFromJson() {
        JobInfoQueryDto fromDto = new JobInfoQueryDto(
            1L, 1, "syn-user", "fooHandler", "jack"
            , new PageRequestDto(1, 100)
        );
        JobInfoQueryDto toDto = JacksonUtil.fromJson(JacksonUtil.toJson(fromDto), JobInfoQueryDto.class);
        assertThat(toDto).isEqualTo(fromDto);
    }
}
