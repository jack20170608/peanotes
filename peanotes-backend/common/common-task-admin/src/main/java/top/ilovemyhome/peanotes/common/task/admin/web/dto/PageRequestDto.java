package top.ilovemyhome.peanotes.common.task.admin.web.dto;

import top.ilovemyhome.peanotes.backend.common.db.dao.page.Direction;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Page;

public record PageRequestDto(int page, int pageSize, String sort,
                             Direction sortDirection) {

    public PageRequestDto() {
        this(Page.FIRST_PAGE, Page.DEFAULT_PAGE_SIZE);
    }

    public PageRequestDto(int page, int pageSize) {
        this(page, pageSize, null, null);
    }

    public PageRequestDto(int page, int pageSize, String sort, Direction sortDirection) {
        this.page = page;
        this.pageSize = pageSize;
        this.sort = sort;
        this.sortDirection = sortDirection;
    }

}
