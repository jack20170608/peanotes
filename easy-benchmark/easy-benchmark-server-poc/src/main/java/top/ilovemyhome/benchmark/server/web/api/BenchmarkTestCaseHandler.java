package top.ilovemyhome.benchmark.server.web.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jdk.jfr.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.benchmark.server.application.AppContext;
import top.ilovemyhome.benchmark.server.dao.BenchmarkTestCaseDao;
import top.ilovemyhome.benchmark.si.BenchmarkTestCase;
import top.ilovemyhome.commons.common.number.IdGenerator;

import java.time.LocalDateTime;
import java.util.*;

@Path("/benchmark-test-cases")
public class BenchmarkTestCaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkTestCaseHandler.class);

    private final BenchmarkTestCaseDao benchmarkTestCaseDao;

    public BenchmarkTestCaseHandler(AppContext appContext) {
        this.benchmarkTestCaseDao = appContext.getBean("benchmarkTestCaseDao", BenchmarkTestCaseDao.class);
    }


    /**
     * Creates a new benchmark test case.
     * <p>
     * If the test case doesn't have an ID or creation/last update timestamps, they will be automatically generated.
     *
     * @param testCase the benchmark test case to create
     * @return the created benchmark test case with generated ID and timestamps
     * @throws IllegalArgumentException if testCase is null
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Description("Creates a new benchmark test case")
    public BenchmarkTestCase create(BenchmarkTestCase testCase) {
        if (testCase == null) {
            throw new IllegalArgumentException("BenchmarkTestCase cannot be null");
        }

        LocalDateTime now = LocalDateTime.now();
        BenchmarkTestCase.Builder builder = BenchmarkTestCase.builder(testCase);
        Long id = testCase.id() != null ? testCase.id() : IdGenerator.getInstance().nextIncrementId();
        builder.withId(id);

        // Set timestamps if not provided
        if (testCase.createDt() == null) {
            builder.withCreateDt(now);
        }
        if (testCase.lastUpdateDt() == null) {
            builder.withLastUpdateDt(now);
        }

        BenchmarkTestCase createdTestCase = builder.build();
        benchmarkTestCaseDao.create(createdTestCase);
        logger.debug("Created BenchmarkTestCase with ID: {}", id);
        return createdTestCase;
    }


    /**
     * Retrieves a benchmark test case by its ID.
     *
     * @param ids the IDs of the benchmark test cases to retrieve
     * @return the benchmark test case with the specified ID, or null if not found
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Description("Retrieves a benchmark test case by its ID")
    public Response getByIds(@QueryParam("ids") List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("IDs cannot be null or empty");
        }
        return Response.ok(benchmarkTestCaseDao.findAllByIds(ids)).build();
    }



    /**
     * Updates an existing benchmark test case.
     * <p>
     * The test case must have a valid ID that exists in the store.
     * The last update timestamp will be automatically updated.
     *
     * @param testCase the benchmark test case to update
     * @return the updated benchmark test case, or null if the test case with the specified ID doesn't exist
     * @throws IllegalArgumentException if testCase or its ID is null
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Description("Updates an existing benchmark test case")
    public Response update(BenchmarkTestCase testCase) {
        if (testCase == null) {
            throw new IllegalArgumentException("BenchmarkTestCase cannot be null");
        }
        if (testCase.id() == null) {
            throw new IllegalArgumentException("BenchmarkTestCase ID cannot be null for update");
        }

        // Check if the test case exists
        if (!benchmarkTestCaseDao.exists(testCase.id())) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("BenchmarkTestCase with ID " + testCase.id() + " not found").build();
        }
        // Update the last update timestamp
        BenchmarkTestCase.Builder builder = BenchmarkTestCase.builder(testCase);
        BenchmarkTestCase updatedTestCase = builder
                .withLastUpdateDt(LocalDateTime.now())
                .build();
        benchmarkTestCaseDao.update(updatedTestCase.id(), updatedTestCase);
        logger.debug("Updated BenchmarkTestCase with ID: {}", testCase.id());
        return Response.ok(updatedTestCase).build();
    }

    /**
     * Deletes a benchmark test case by its ID.
     *
     * @param id the ID of the benchmark test case to delete
     * @return true if the test case was deleted, false if it doesn't exist
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Description("Deletes a benchmark test case by its ID")
    public Response delete(@PathParam("id") Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        int deletedCount = benchmarkTestCaseDao.delete(id);
        if (deletedCount > 0) {
            logger.debug("Deleted BenchmarkTestCase with ID: {}", id);
        }
        return deletedCount > 0 ? Response.ok().build()
            : Response.status(Response.Status.NOT_FOUND).build();
    }
}//:~)
