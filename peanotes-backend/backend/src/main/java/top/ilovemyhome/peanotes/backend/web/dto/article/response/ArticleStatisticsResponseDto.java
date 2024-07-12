package top.ilovemyhome.peanotes.backend.web.dto.article.response;


import java.io.Serializable;

/**
 * 文章统计对象
 *
 * @author xzzz
 */
public class ArticleStatisticsResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer articleCount;

    private Integer articleWords;

    public ArticleStatisticsResponseDto(Integer articleCount, Integer articleWords) {
        this.articleCount = articleCount;
        this.articleWords = articleWords;
    }

    public Integer getArticleCount() {
        return articleCount;
    }

    public Integer getArticleWords() {
        return articleWords;
    }
}
