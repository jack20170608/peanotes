package top.ilovemyhome.peanotes.backend.web.dto.article.request;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 文章修改请求
 *
 */
public class ArticleUpdateRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Min(value = 0, message = "[文章ID] 不能小于0")
    @NotNull(message = "[文章ID] 为必填项")
    private Long id;
    /**
     * 文件夹ID
     */
    @Min(value = 0, message = "[上级菜单ID] 不能小于0")
    @NotNull(message = "[上级菜单] 为必填项")
    private Long pid;
    /**
     * 文章名称
     */
    @NotBlank(message = "[文章名称] 为必填项")
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
    /** 颜色 */
    private String color;

    public ArticleUpdateRequestDto(Long id, Long pid, String name, String icon, List<String> tags, Integer sort, String cover, String describes, String color) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.icon = icon;
        this.tags = tags;
        this.sort = sort;
        this.cover = cover;
        this.describes = describes;
        this.color = color;
    }

    public @Min(value = 0, message = "[文章ID] 不能小于0") @NotNull(message = "[文章ID] 为必填项") Long getId() {
        return id;
    }

    public @Min(value = 0, message = "[上级菜单ID] 不能小于0") @NotNull(message = "[上级菜单] 为必填项") Long getPid() {
        return pid;
    }

    public @NotBlank(message = "[文章名称] 为必填项") String getName() {
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

    public String getColor() {
        return color;
    }
}
