package top.ilovemyhome.peanotes.backend.web.dto.article.response;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ArticleInfoResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;
    /**
     * 文件夹ID
     */
    private Long pid;
    /**
     * 文章名称
     */
    private String name;
    /**
     * 文章图标
     */
    private String icon;
    /**
     * 标签集合
     */
    private List<String> tags;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 封面
     */
    private String cover;
    /**
     * 描述
     */
    private String describes;
    /**
     * star状态
     */
    private Integer starStatus;
    /**
     * 页面的查看数
     */
    private Integer pv;
    /**
     * 独立的访问次数,每日IP重置
     */
    private Integer uv;
    /**
     * 点赞数
     */
    private Integer likes;
    /**
     * 文章字数
     */
    private Integer words;
    /**
     * 版本
     */
    private Integer version;
    /**
     * Markdown 内容
     */
    private String markdown;
    /**
     * Html 内容
     */
    private String html;
    /**
     * 版本
     */
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    private LocalDate updateTime;
    /**
     * 颜色
     */
    private String color;
    /**
     * 目录
     */
    private String toc;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 公开状态
     */
    private Integer openStatus;
    /**
     * 公开同步时间
     */
    private LocalDateTime syncTime;
    /**
     * 公开时间
     */
    private LocalDateTime openTime;
    /**
     * 公开版本
     */
    private Integer openVersion;


    public Long getId() {
        return id;
    }

    public Long getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public List<String> getTags() {
        return tags;
    }

    public Integer getSort() {
        return sort;
    }

    public String getCover() {
        return cover;
    }

    public String getDescribes() {
        return describes;
    }

    public Integer getStarStatus() {
        return starStatus;
    }

    public Integer getPv() {
        return pv;
    }

    public Integer getUv() {
        return uv;
    }

    public Integer getLikes() {
        return likes;
    }

    public Integer getWords() {
        return words;
    }

    public Integer getVersion() {
        return version;
    }

    public String getMarkdown() {
        return markdown;
    }

    public String getHtml() {
        return html;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public LocalDate getUpdateTime() {
        return updateTime;
    }

    public String getColor() {
        return color;
    }

    public String getToc() {
        return toc;
    }

    public Integer getType() {
        return type;
    }

    public Integer getOpenStatus() {
        return openStatus;
    }

    public LocalDateTime getSyncTime() {
        return syncTime;
    }

    public LocalDateTime getOpenTime() {
        return openTime;
    }

    public Integer getOpenVersion() {
        return openVersion;
    }
}
