package top.ilovemyhome.peanotes.backend.web.dto.article.request;



import java.io.Serializable;
import java.util.List;

public class ArticleQueryRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private List<Long> pids;

    private Integer starStatus;

    private Integer openStatus;

    private Long userId;

    private PageRequestDto pageRequestDto;

    public ArticleQueryRequestDto(Long id, List<Long> pids, Integer starStatus, Integer openStatus, Long userId, PageRequestDto pageRequestDto) {
        this.id = id;
        this.pids = pids;
        this.starStatus = starStatus;
        this.openStatus = openStatus;
        this.userId = userId;
        this.pageRequestDto = pageRequestDto;
    }

    public Long getId() {
        return id;
    }

    public List<Long> getPids() {
        return pids;
    }

    public Integer getStarStatus() {
        return starStatus;
    }

    public Integer getOpenStatus() {
        return openStatus;
    }

    public Long getUserId() {
        return userId;
    }

    public PageRequestDto getPageRequestDto() {
        return pageRequestDto;
    }
}
