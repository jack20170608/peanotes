package top.ilovemyhome.peanotes.common.task.exe.common;

import com.google.common.collect.ImmutableSet;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.assertj.core.util.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.exe.domain.HandleCallbackParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.RegistryParam;
import top.ilovemyhome.peanotes.common.task.exe.domain.TaskResponse;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Path("/task/api")
public class TaskAdminController {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("callback")
    public Response callback(ArrayList<HandleCallbackParam> callbackParamList) {
        LOGGER.info("Received [{}]", callbackParamList);
        try {
            return Response.ok(TaskResponse.SUCCESS).build();
        }catch (Throwable t){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(t.getMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("register")
    public Response register(RegistryParam registryParam) {
        LOGGER.info("Register request with parameter=[{}].", registryParam);
        try {
            String appName = registryParam.appName();
            Set<String> addresses = nameAddressMap.getOrDefault(appName, Sets.newHashSet());
            addresses.add(registryParam.address());
            nameAddressMap.put(appName, addresses);
            return Response.ok(TaskResponse.SUCCESS).build();
        }catch (Throwable t){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(t.getMessage()).build();
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("unregister")
    public Response unregister(RegistryParam registryParam) {
        LOGGER.info("Unregister request with parameter=[{}].", registryParam);
        try {
            String appName = registryParam.appName();
            Set<String> addresses = nameAddressMap.get(appName);
            if (Objects.nonNull(addresses)){
                addresses.remove(registryParam.address());
            }
            return Response.ok(TaskResponse.SUCCESS).build();
        }catch (Throwable t){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(t.getMessage()).build();
        }
    }

    public Set<String> getAddressSet(String appName){
        return ImmutableSet.copyOf(
            nameAddressMap.getOrDefault(appName, Sets.newHashSet())
        );
    }

    private static final Map<String, Set<String>> nameAddressMap = new ConcurrentHashMap<>();

    private static final AtomicLong ID_GENERATOR = new AtomicLong();
    private static final AtomicInteger SEQ_GENERATOR = new AtomicInteger();



    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAdminController.class);
}
