package top.ilovemyhome.peanotes.backend.web.dto.reference;

/**
 * @author xzzz
 */
public class ArticleReferenceRequestDto {
    /**
     * 引用ID
     */
    private Long targetId;
    /**
     * 引用名称, 链接名称或图片名称
     */
    private String targetName;
    /**
     * 引用地址, 链接的地址或图片地址
     */
    private String targetUrl;
    /**
     * 类型 [暂无]
     */
    private Integer type;

    public ArticleReferenceRequestDto(Long targetId, String targetName, String targetUrl, Integer type) {
        this.targetId = targetId;
        this.targetName = targetName;
        this.targetUrl = targetUrl;
        this.type = type;
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
}
