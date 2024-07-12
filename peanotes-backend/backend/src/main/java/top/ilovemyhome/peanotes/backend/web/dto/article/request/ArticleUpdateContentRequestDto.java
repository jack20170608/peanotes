package top.ilovemyhome.peanotes.backend.web.dto.article.request;


import top.ilovemyhome.peanotes.backend.web.dto.reference.ArticleReferenceRequestDto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 修改文章内容
 *
 */
public class ArticleUpdateContentRequestDto {

    /**
     * ID
     */
    @Min(value = 0, message = "[文章ID] 不能小于0")
    @NotNull(message = "[文章ID] 为必填项")
    private Long id;
    /**
     * 名称, 用于引用关系表中的名称冗余
     */
    @NotBlank(message = "文章名称为必填项")
    private String name;
    /**
     * markdown 内容
     */
    private String markdown;
    /**
     * html 内容
     */
    private String html;
    /**
     * 目录
     */
    private String toc;
    /**
     * 引用链接集合
     */
    private List<ArticleReferenceRequestDto> references;
}
