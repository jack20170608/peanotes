package top.ilovemyhome.peanotes.backend.domain.reference;

import java.io.Serializable;

/**
 * 文章图片引用关系
 *
 * @author xzzz
 */
public class ArticleReferenceEntity implements Serializable {

    /**
     * ID
     */
    private Long id;
    /**
     * 文章ID
     */
    private Long sourceId;
    /**
     * 文章名称
     */
    private String sourceName;
    /**
     * 引用文章ID
     */
    private Long targetId;
    /**
     * 引用名称
     */
    private String targetName;
    /**
     * 引用链接
     */
    private String targetUrl;
    /**
     * 引用类型: 10:图片; 11:文章; 21:外部文章
     */
    private Integer type;
    /**
     * 用户ID
     */
    private Long userId;

    private ArticleReferenceEntity(Long id, Long sourceId, String sourceName, Long targetId, String targetName, String targetUrl, Integer type, Long userId) {
        this.id = id;
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.targetId = targetId;
        this.targetName = targetName;
        this.targetUrl = targetUrl;
        this.type = type;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public Long getTargetId() {
        return targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public Integer getType() {
        return type;
    }

    public Long getUserId() {
        return userId;
    }
}
