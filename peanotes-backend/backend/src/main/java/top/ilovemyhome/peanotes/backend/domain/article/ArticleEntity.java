package top.ilovemyhome.peanotes.backend.domain.article;


import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class ArticleEntity implements Serializable {

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

    public String getTags() {
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

    public Integer getOpenStatus() {
        return openStatus;
    }

    public Integer getOpenVersion() {
        return openVersion;
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

    public String getColor() {
        return color;
    }

    public String getToc() {
        return toc;
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

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDate getLastEditTime() {
        return lastEditTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleEntity that = (ArticleEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(pid, that.pid) && Objects.equals(name, that.name) && Objects.equals(icon, that.icon) && Objects.equals(tags, that.tags) && Objects.equals(sort, that.sort) && Objects.equals(cover, that.cover) && Objects.equals(describes, that.describes) && Objects.equals(starStatus, that.starStatus) && Objects.equals(openStatus, that.openStatus) && Objects.equals(openVersion, that.openVersion) && Objects.equals(pv, that.pv) && Objects.equals(uv, that.uv) && Objects.equals(likes, that.likes) && Objects.equals(words, that.words) && Objects.equals(version, that.version) && Objects.equals(color, that.color) && Objects.equals(toc, that.toc) && Objects.equals(markdown, that.markdown) && Objects.equals(html, that.html) && Objects.equals(createTime, that.createTime) && Objects.equals(updateTime, that.updateTime) && Objects.equals(userId, that.userId) && Objects.equals(lastEditTime, that.lastEditTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pid, name, icon, tags, sort, cover, describes, starStatus, openStatus, openVersion, pv, uv, likes, words, version, color, toc, markdown, html, createTime, updateTime, userId, lastEditTime);
    }

    public static Builder builder(){
        return new Builder();
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long pid;

    private String name;
    /**
     * 文章图标
     */
    private String icon;

    /**
     * 标签集合
     */
    private String tags;
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
     * 公开状态
     */
    private Integer openStatus;
    /**
     * 公开版本
     */
    private Integer openVersion;
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

    private Integer version;

    private String color;

    private String toc;

    private String markdown;

    private String html;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long userId;

    /**
     * 文章内容的修改时间
     */
    private LocalDate lastEditTime;

    private ArticleEntity(Long id, Long pid, String name, String icon, String tags, Integer sort, String cover, String describes, Integer starStatus, Integer openStatus, Integer openVersion, Integer pv, Integer uv, Integer likes, Integer words, Integer version, String color, String toc, String markdown, String html, LocalDateTime createTime, LocalDateTime updateTime, Long userId, LocalDate lastEditTime) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.icon = icon;
        this.tags = tags;
        this.sort = sort;
        this.cover = cover;
        this.describes = describes;
        this.starStatus = starStatus;
        this.openStatus = openStatus;
        this.openVersion = openVersion;
        this.pv = pv;
        this.uv = uv;
        this.likes = likes;
        this.words = words;
        this.version = version;
        this.color = color;
        this.toc = toc;
        this.markdown = markdown;
        this.html = html;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.userId = userId;
        this.lastEditTime = lastEditTime;
    }


    public static final class Builder {
        private Long id;
        private Long folderId;
        private String name;
        private String icon;
        private String tags;
        private Integer sort;
        private String cover;
        private String describes;
        private Integer starStatus;
        private Integer openStatus;
        private Integer openVersion;
        private Integer pv;
        private Integer uv;
        private Integer likes;
        private Integer words;
        private Integer version;
        private String color;
        private String toc;
        private String markdown;
        private String html;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
        private Long userId;
        private LocalDate lastEditTime;

        private Builder() {
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withFolderId(Long folderId) {
            this.folderId = folderId;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withIcon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder withTags(String tags) {
            this.tags = tags;
            return this;
        }

        public Builder withSort(Integer sort) {
            this.sort = sort;
            return this;
        }

        public Builder withCover(String cover) {
            this.cover = cover;
            return this;
        }

        public Builder withDescribes(String describes) {
            this.describes = describes;
            return this;
        }

        public Builder withStarStatus(Integer starStatus) {
            this.starStatus = starStatus;
            return this;
        }

        public Builder withOpenStatus(Integer openStatus) {
            this.openStatus = openStatus;
            return this;
        }

        public Builder withOpenVersion(Integer openVersion) {
            this.openVersion = openVersion;
            return this;
        }

        public Builder withPv(Integer pv) {
            this.pv = pv;
            return this;
        }

        public Builder withUv(Integer uv) {
            this.uv = uv;
            return this;
        }

        public Builder withLikes(Integer likes) {
            this.likes = likes;
            return this;
        }

        public Builder withWords(Integer words) {
            this.words = words;
            return this;
        }

        public Builder withVersion(Integer version) {
            this.version = version;
            return this;
        }

        public Builder withColor(String color) {
            this.color = color;
            return this;
        }

        public Builder withToc(String toc) {
            this.toc = toc;
            return this;
        }

        public Builder withMarkdown(String markdown) {
            this.markdown = markdown;
            return this;
        }

        public Builder withHtml(String html) {
            this.html = html;
            return this;
        }

        public Builder withCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder withUpdateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Builder withUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder withLastEditTime(LocalDate lastEditTime) {
            this.lastEditTime = lastEditTime;
            return this;
        }

        public ArticleEntity build() {
            return new ArticleEntity(id, folderId, name, icon, tags, sort, cover, describes, starStatus, openStatus, openVersion, pv, uv, likes, words, version, color, toc, markdown, html, createTime, updateTime, userId, lastEditTime);
        }
    }
}
