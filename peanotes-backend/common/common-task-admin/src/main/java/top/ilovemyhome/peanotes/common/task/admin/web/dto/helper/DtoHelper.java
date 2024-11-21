package top.ilovemyhome.peanotes.common.task.admin.web.dto.helper;

import top.ilovemyhome.peanotes.backend.common.db.dao.page.impl.PageRequest;
import top.ilovemyhome.peanotes.common.task.admin.web.dto.PageRequestDto;

public final class DtoHelper {

    public static PageRequest toPageRequest(PageRequestDto dto){
        return new PageRequest(dto.page(), dto.pageSize(), dto.sortDirection(), dto.sort());
    }

    private DtoHelper() {
    }
}
