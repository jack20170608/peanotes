package top.ilovemyhome.peanotes.backend.web.dto.article.response;


import java.io.Serializable;
import java.time.LocalDate;

/**
 * 修改文章内容
 */
public class ArticleUpdateContentResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long id;
    /**
     * 文章版本
     */
    private Integer version;
    /**
     * 字数
     */
    private Integer words;
    /**
     * 修改时间
     */
    private LocalDate updateTime;

    public ArticleUpdateContentResponseDto(Long id, Integer version, Integer words, LocalDate updateTime) {
        this.id = id;
        this.version = version;
        this.words = words;
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    public Integer getWords() {
        return words;
    }

    public LocalDate getUpdateTime() {
        return updateTime;
    }
}
