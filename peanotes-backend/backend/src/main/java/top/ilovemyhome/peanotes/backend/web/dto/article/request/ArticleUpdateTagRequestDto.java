package top.ilovemyhome.peanotes.backend.web.dto.article.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 添加,取消标签
 *
 */
public class ArticleUpdateTagRequestDto {

    /**
     * ID
     */
    @Min(value = 0, message = "[文章ID] 不能小于0")
    @NotNull(message = "[文章ID] 为必填项")
    private Long id;
    /**
     * 标签, toc 标签因为具有特殊意义, 必须小写
     */
    @NotBlank(message = "标签为必填项")
    private String tag;

    public ArticleUpdateTagRequestDto(Long id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }
}
