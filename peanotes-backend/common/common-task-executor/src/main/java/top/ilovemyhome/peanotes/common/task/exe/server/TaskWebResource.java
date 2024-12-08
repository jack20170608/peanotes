package top.ilovemyhome.peanotes.common.task.exe.server;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.exe.TaskExecutor;
import top.ilovemyhome.peanotes.common.task.exe.domain.IdleBeatParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.KillParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.LogParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.TriggerParam;

@Path("/api/v1/task")
public class TaskWebResource {


    public TaskWebResource(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @GET
    @Path("/beat")
    @Produces(MediaType.TEXT_PLAIN)
    public Response beat(){
        return Response.ok(taskExecutor.beat()).build();
    }

    @POST
    @Path("/idleBeat")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response idleBeat(IdleBeatParam idleBeatParam){
        return Response.ok().build();
    }

    @POST
    @Path("/run")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response run(TriggerParam triggerParam){
        return Response.ok().build();
    }

    @POST
    @Path("/kill")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response run(KillParam killParam){
        return Response.ok().build();
    }

    @POST
    @Path("/log")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response log(LogParam logParam){
        return Response.ok().build();
    }

    private final TaskExecutor taskExecutor;

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskWebResource.class);

}
