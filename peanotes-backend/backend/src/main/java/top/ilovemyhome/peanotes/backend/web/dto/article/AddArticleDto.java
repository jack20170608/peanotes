package top.ilovemyhome.peanotes.backend.web.dto.article;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class AddArticleDto implements Serializable {

    @Min(value = 0, message = "[上级菜单ID] 不能小于0")
    @NotNull(message = "[上级菜单] 为必填项")
    private Long pid;

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
    /**
     * 颜色
     */
    private String color;
    /**
     * 是否新增到尾部, 将忽略传入的 sort, 使用最大 sort
     *
     * @since 1.10.0
     */
    private Boolean addToLast;

    public @Min(value = 0, message = "[上级菜单ID] 不能小于0") @NotNull(message = "[上级菜单] 为必填项") Long getPid() {
        return pid;
    }

    public void setPid(@Min(value = 0, message = "[上级菜单ID] 不能小于0") @NotNull(message = "[上级菜单] 为必填项") Long pid) {
        this.pid = pid;
    }

    public @NotBlank(message = "[文章名称] 为必填项") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "[文章名称] 为必填项") String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getAddToLast() {
        return addToLast;
    }

    public void setAddToLast(Boolean addToLast) {
        this.addToLast = addToLast;
    }
}
