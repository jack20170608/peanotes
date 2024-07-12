package top.ilovemyhome.peanotes.backend.web.dto.article.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ArticleStarRequestDto {

    @Min(value = 0, message = "[文章ID] 不能小于0")
    @NotNull(message = "[文章ID] 为必填项")
    private Long id;

    @Min(value = 0, message = "[star 状态] 不能小于0")
    @Max(value = 1, message = "[star 状态] 不能大于1")
    @NotNull(message = "[star 状态] 为必填项")
    private Integer starStatus;

    public ArticleStarRequestDto(Long id, Integer starStatus) {
        this.id = id;
        this.starStatus = starStatus;
    }

    public  Long getId() {
        return id;
    }

    public Integer getStarStatus() {
        return starStatus;
    }
}
