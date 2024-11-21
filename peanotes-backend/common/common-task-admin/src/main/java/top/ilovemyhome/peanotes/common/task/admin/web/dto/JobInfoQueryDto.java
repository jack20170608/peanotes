package top.ilovemyhome.peanotes.common.task.admin.web.dto;

public record JobInfoQueryDto(Long jobGroupId, int triggerStatus, String jobDesc, String executorHandler, String author, PageRequestDto pageRequest) {

    public JobInfoQueryDto(Long jobGroupId, int triggerStatus, String jobDesc, String executorHandler, String author) {
        this(jobGroupId, triggerStatus, jobDesc, executorHandler, author, new PageRequestDto(0, 20));
    }

    public JobInfoQueryDto(Long jobGroupId, int triggerStatus, String jobDesc, String executorHandler, String author, PageRequestDto pageRequest) {
        this.jobGroupId = jobGroupId;
        this.triggerStatus = triggerStatus;
        this.jobDesc = jobDesc;
        this.executorHandler = executorHandler;
        this.author = author;
        this.pageRequest = pageRequest;
    }
}
