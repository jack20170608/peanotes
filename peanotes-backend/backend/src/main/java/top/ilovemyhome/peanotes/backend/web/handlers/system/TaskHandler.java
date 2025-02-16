package top.ilovemyhome.peanotes.backend.web.handlers.system;

import io.muserver.rest.Required;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jdk.jfr.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.common.task.*;
import top.ilovemyhome.peanotes.backend.common.task.impl.StringTaskOutput;
import top.ilovemyhome.peanotes.backend.common.task.persistent.TaskOrder;
import top.ilovemyhome.peanotes.backend.common.task.persistent.TaskRecord;

import java.util.List;
import java.util.Objects;


@Path("/task/api/v1")
public class TaskHandler {

    @GET
    @Path("/getByOrder")
    @Produces(MediaType.APPLICATION_JSON)
    @Description("Get all tasks by the given order")
    public Response getByOrder(@QueryParam("orderKey") String orderKey) {
        try {
            List<TaskRecord> taskRecords = taskDagService.findTaskByOrderKey(orderKey);
            if (Objects.isNull(taskRecords) || taskRecords.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("No tasks found any tasks with order key {" + orderKey + "}")
                    .build();
            }
            return Response.ok(taskRecords).build();
        } catch (Throwable t) {
            LOGGER.warn("Query task failure", t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @POST
    @Path("/createOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrder(TaskOrder simpleTaskOrder) {
        Long id = taskDagService.createOrder(simpleTaskOrder);
        simpleTaskOrder.setId(id);
        LOGGER.info("Created new task order {}", simpleTaskOrder);
        return Response.ok(simpleTaskOrder).build();
    }

    @POST
    @Path("/updateOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrder(TaskOrder simpleTaskOrder) {
        String key = simpleTaskOrder.getKey();
        int updateStatus = taskDagService.updateOrderByKey(key, simpleTaskOrder);
        LOGGER.info("Updated task order {}, with result {}.", simpleTaskOrder, updateStatus);
        if (updateStatus == 1) {
            return Response.ok("OK").build();
        }else {
            return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Description("Create new task records")
    public Response create(List<TaskRecord> taskRecords) {
        try {
            List<Long> ids = taskDagService.createTasks(taskRecords);
            return Response.ok(ids).build();
        } catch (Throwable t) {
            LOGGER.warn("Query task failure", t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @PUT
    @Path("/loadAndStart")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("*/*")
    public Response loadAndStart(@QueryParam("orderKey") String orderKey) {
        try {
            boolean isOrdered = taskDagService.isOrdered(orderKey);
            if (!isOrdered) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("The order key don't exists.").build();
            }
            taskDagService.loadAndStart(orderKey);
            return Response.ok("OK").build();
        } catch (Throwable t) {
            LOGGER.warn("Query task failure", t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @GET
    @Path("/getNextTaskIds")
    @Produces(MediaType.APPLICATION_JSON)
    @Description("Get next batch of task ids")
    public Response getNextTaskIds(@QueryParam("count") int count) {
        try {
            LOGGER.info("Get next task ids count {}", count);
            return Response.ok(taskDagService.getNextTaskIds(count)).build();
        } catch (Throwable t) {
            LOGGER.warn("Query task failure", t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @PUT
    @Path("/receiveTaskEvent")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response receiveTaskEvent(@QueryParam("taskId") Long taskId
        , @QueryParam("taskStatus") @Required @DefaultValue("SUCCESS") TaskStatus taskStatus, StringTaskOutput taskOutput) {
        try {
            taskDagService.receiveTaskEvent(taskId, taskStatus, taskOutput);
        }catch (Throwable t){
            LOGGER.warn("Query task failure", t);
            return Response.serverError().entity(t.getMessage()).build();
        }
        return Response.ok(taskDagService.getByIds(List.of(taskId)).getFirst()).build();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskHandler.class);

    public TaskHandler(AppContext appContext) {
        this.taskContext = appContext.getTaskContext();
        this.taskDagService = taskContext.getTaskDagService();
    }

    private final TaskContext taskContext;
    private final TaskDagService taskDagService;
}
