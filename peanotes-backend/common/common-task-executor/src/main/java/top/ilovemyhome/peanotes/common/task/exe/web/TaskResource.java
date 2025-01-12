package top.ilovemyhome.peanotes.common.task.exe.web;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.exe.TaskExecutor;
import top.ilovemyhome.peanotes.common.task.exe.TaskExecutorContext;
import top.ilovemyhome.peanotes.common.task.exe.domain.IdleBeatParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.KillParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.LogParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.TriggerParam;

import java.util.function.Supplier;

@Path("/task/v1")
public class TaskResource {


    public TaskResource(TaskExecutorContext executorContext) {
        this.taskExecutorSupplier = () -> executorContext.getTaskExecutor();
    }

    @GET
    @Path("/beat")
    @Produces(MediaType.TEXT_PLAIN)
    public Response beat(){
        LOGGER.info("Beat request.");
        return Response.ok(taskExecutorSupplier.get().beat()).build();
    }

    @POST
    @Path("/idleBeat")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response idleBeat(IdleBeatParam idleBeatParam){
        return Response.ok(taskExecutorSupplier.get().idleBeat(idleBeatParam))
            .build();
    }

    @POST
    @Path("/run")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response run(TriggerParam triggerParam){
        return Response.ok(taskExecutorSupplier.get().run(triggerParam)).build();
    }

    @POST
    @Path("/kill")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response run(KillParam killParam){
        return Response.ok(taskExecutorSupplier.get().kill(killParam)).build();
    }

    @POST
    @Path("/log")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response log(LogParam logParam){
        taskExecutorSupplier.get().log(logParam);
        return Response.ok(taskExecutorSupplier.get().log(logParam)).build();
    }

    private final Supplier<TaskExecutor> taskExecutorSupplier;

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskResource.class);

}
