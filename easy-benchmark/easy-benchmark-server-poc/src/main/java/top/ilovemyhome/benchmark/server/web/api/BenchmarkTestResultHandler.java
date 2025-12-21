package top.ilovemyhome.benchmark.server.web.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.benchmark.server.application.AppContext;
import top.ilovemyhome.benchmark.server.dao.BenchmarkTestResultDao;
import top.ilovemyhome.benchmark.si.BenchmarkTestResult;
import top.ilovemyhome.benchmark.si.enums.State;
import top.ilovemyhome.commons.common.number.IdGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Path("/benchmark-test-results")
public class BenchmarkTestResultHandler {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkTestResultHandler.class);
    private final BenchmarkTestResultDao benchmarkTestResultDao;

    public BenchmarkTestResultHandler(AppContext appContext) {
        this.benchmarkTestResultDao = appContext.getBean("benchmarkTestResultDao", BenchmarkTestResultDao.class);
    }

    /**
     * Creates a new benchmark test result.
     *
     * @param testResult the benchmark test result to create
     * @return the created benchmark test result with generated ID and timestamps
     * @throws IllegalArgumentException if testResult is null
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(BenchmarkTestResult testResult) {
        if (testResult == null) {
            throw new IllegalArgumentException("BenchmarkTestResult cannot be null");
        }
        LocalDateTime now = LocalDateTime.now();
        BenchmarkTestResult.Builder builder = BenchmarkTestResult.builder(testResult);
        Long id = testResult.id() != null ? testResult.id() : IdGenerator.getInstance().nextIncrementId();
        builder.withId(id);

        // Set default state if not provided
        if (testResult.state() == null) {
            builder.withState(State.INIT);
        }

        // Set timestamps if not provided
        if (testResult.createDt() == null) {
            builder.withCreateDt(now);
        }
        if (testResult.lastUpdateDt() == null) {
            builder.withLastUpdateDt(now);
        }
        BenchmarkTestResult createdResult = builder.build();
        benchmarkTestResultDao.create(createdResult);
        logger.debug("Created BenchmarkTestResult with ID: {}", id);
        return Response.ok(createdResult).build();
    }


    /**
     * Retrieves benchmark test results by multiple IDs.
     *
     * @param ids the IDs of the benchmark test results to retrieve
     * @return a list of benchmark test results with the specified IDs
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByIds(@QueryParam("ids") List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("IDs cannot be null or empty");
        }
        return Response.ok(benchmarkTestResultDao.findAllByIds(ids)).build();
    }


    /**
     * Updates an existing benchmark test result.
     *
     * @param id         the ID of the benchmark test result to update
     * @param testResult the updated benchmark test result
     * @return the updated benchmark test result, or 404 if not found
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, BenchmarkTestResult testResult) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (testResult == null) {
            throw new IllegalArgumentException("BenchmarkTestResult cannot be null");
        }
        if (!id.equals(testResult.id())) {
            throw new IllegalArgumentException("ID in path must match ID in request body");
        }

        // Check if the test result exists
        if (!benchmarkTestResultDao.exists(id)) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("BenchmarkTestResult with ID " + id + " not found").build();
        }

        // Update the last update timestamp
        BenchmarkTestResult.Builder builder = BenchmarkTestResult.builder(testResult);
        BenchmarkTestResult updatedResult = builder
            .withLastUpdateDt(LocalDateTime.now())
            .build();

        benchmarkTestResultDao.update(id, updatedResult);
        logger.debug("Updated BenchmarkTestResult with ID: {}", id);
        return Response.ok(updatedResult).build();
    }

    /**
     * Deletes a benchmark test result by its ID.
     *
     * @param id the ID of the benchmark test result to delete
     * @return 204 if deleted, or 404 if not found
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        int deletedCount = benchmarkTestResultDao.delete(id);
        if (deletedCount > 0) {
            logger.debug("Deleted BenchmarkTestResult with ID: {}", id);
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
