package top.ilovemyhome.peanotes.backend.web.dto.article.request;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 修改文章名称
 *
 * @since 1.10.0
 */
public class ArticleUpdateNameRequestDto {

    /**
     * ID
     */
    @Min(value = 0, message = "[文章ID] 不能小于0")
    @NotNull(message = "[文章ID] 为必填项")
    private Long id;
    /**
     * 名称
     */
    @NotBlank(message = "文章名称为必填项")
    private String name;

    public ArticleUpdateNameRequestDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
