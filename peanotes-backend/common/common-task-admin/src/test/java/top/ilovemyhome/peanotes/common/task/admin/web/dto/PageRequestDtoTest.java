package top.ilovemyhome.peanotes.common.task.admin.web.dto;

import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Direction;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PageRequestDtoTest {

    @Test
    public void testFromJson(){
        PageRequestDto fromDto = new PageRequestDto(10, 100, "id",Direction.ASC);
        PageRequestDto toDto = JacksonUtil.fromJson(JacksonUtil.toJson(fromDto), PageRequestDto.class);
        assertThat(toDto).isEqualTo(fromDto);
    }
}
