package top.ilovemyhome.peanotes.common.task.admin.web.handlers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Page;
import top.ilovemyhome.peanotes.common.task.admin.application.AppContext;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobInfo;
import top.ilovemyhome.peanotes.common.task.admin.service.JobService;
import top.ilovemyhome.peanotes.common.task.admin.web.dto.JobInfoQueryDto;
import top.ilovemyhome.peanotes.common.task.admin.web.handlers.helper.R;

@Path("/jobinfo")
public class JobInfoHandler {

    @POST
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public R<Page<JobInfo>> query(JobInfoQueryDto jobInfoQueryDto) {
        LOGGER.info("Query=[{}].", jobInfoQueryDto);
        try {
            Page<JobInfo> result = jobService.query(jobInfoQueryDto);
            LOGGER.info("Result size={}.", result.getContent().size());
            return R.ok(result);
        } catch (Throwable t) {
            LOGGER.warn("Error", t);
            return R.fail(t.getMessage());
        }
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public R<Long> add(JobInfo jobInfo){
        return jobService.add(jobInfo);
    }



    public JobInfoHandler(AppContext appContext) {
        this.jobService = appContext.getBean("jobService", JobService.class);
    }

    private final JobService jobService;

    private static final Logger LOGGER = LoggerFactory.getLogger(JobInfoHandler.class);
}
