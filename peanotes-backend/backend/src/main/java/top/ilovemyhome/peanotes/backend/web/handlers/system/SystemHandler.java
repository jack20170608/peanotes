package top.ilovemyhome.peanotes.backend.web.handlers.system;

import io.muserver.rest.Description;
import io.muserver.rest.Required;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Direction;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Page;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.impl.PageRequest;
import top.ilovemyhome.peanotes.backend.domain.system.SystemParamEntity;
import top.ilovemyhome.peanotes.backend.service.system.SystemParamCrudService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Path("/system/api/v1")
public class SystemHandler {

    public SystemHandler(AppContext appContext) {
        this.appContext = appContext;
        this.systemParamCrudService = appContext.getBean("systemParamCrudService"
            , SystemParamCrudService.class);
    }

    @GET
    @Path("/param/getByIds")
    @Produces(MediaType.APPLICATION_JSON)
    @Description(value = "Prove the dao level is working with page query.")
    public Response getByIds(@QueryParam("ids") List<Long> ids) {
        try {
            List<SystemParamEntity> result = systemParamCrudService.getByIds(ids);
            LOGGER.info("Result size={}.", result.size());
            return Response.ok(result).build();
        } catch (Throwable t) {
            LOGGER.warn("Error", t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @GET
    @Path("/param/queryWithKeyword")
    @Produces(MediaType.APPLICATION_JSON)
    @Description(value = "Prove the dao level is working with page query.")
    public Response queryWithPage(@QueryParam("keyword") @DefaultValue("peanote") String keyword
        , @QueryParam("page") @DefaultValue("0") int page
        , @QueryParam("pageSize") @DefaultValue("100") int pageSize
        , @QueryParam("sortBy") @DefaultValue("ID") String sortBy
        , @QueryParam("direction") @DefaultValue("ASC") Direction direction
    ) {
        try {
            PageRequest pageRequest = new PageRequest(page, pageSize, direction, sortBy);
            Page<SystemParamEntity> result = systemParamCrudService.find(keyword, pageRequest);
            LOGGER.info("Result size={}.", result.getContent().size());
            return Response.ok(result).build();
        } catch (Throwable t) {
            LOGGER.warn("Error", t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @PUT
    @Path("/param/put")
    @Produces(MediaType.APPLICATION_JSON)
    @Description("Update or Insert the system parameter with given name and insert new if not exists.")
    public Response put(@QueryParam("paramName") @Required String paramName
        , @QueryParam("paramValue") @Required String paramValue, @QueryParam("paramDesc") String paramDesc) {
        if (StringUtils.isBlank(paramName) || StringUtils.isBlank(paramValue)) {
            throw new BadRequestException("Parameter name or value is empty.");
        }
        try {
            LocalDateTime now = LocalDateTime.now();
            SystemParamEntity.Builder builder = systemParamCrudService.findByParamName(paramName)
                .map(old -> SystemParamEntity.builder(old)
                    .withUpdateDt(now)
                )
                .orElse(SystemParamEntity.builder()
                    .withCreateDt(now)
                    .withUpdateDt(now)
                )
                .withParamName(paramName)
                .withParamValue(paramValue)
                .withParamDesc(paramDesc);
            return Response.ok(systemParamCrudService.put(builder.build())).build();
        } catch (Throwable t) {
            LOGGER.warn("Error", t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @POST
    @Path("/param/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Description("Update the system parameter with id")
    public Response update(@QueryParam("id") Long id, SystemParamEntity entity) {
        if (Objects.isNull(id) || id == 0L) {
            throw new BadRequestException("ID value is null.");
        }
        LocalDateTime now = LocalDateTime.now();
        try {
            Optional<SystemParamEntity> systemParam = systemParamCrudService.getById(id);
            if (systemParam.isEmpty()) {
                throw new BadRequestException("Cannot not find given system param with id " + id);
            }
            SystemParamEntity updatedEntity = SystemParamEntity.builder(entity).withId(id)
                .withUpdateDt(now)
                .build();
            return Response.ok(systemParamCrudService.put(updatedEntity)).build();
        } catch (Throwable t) {
            LOGGER.warn("Error", t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(SystemHandler.class);

    private final SystemParamCrudService systemParamCrudService;
    private final AppContext appContext;
}
