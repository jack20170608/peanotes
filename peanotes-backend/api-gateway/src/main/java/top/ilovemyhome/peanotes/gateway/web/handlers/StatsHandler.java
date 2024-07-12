package top.ilovemyhome.peanotes.gateway.web.handlers;

import com.hsbc.cranker.mucranker.CrankerRouter;
import io.muserver.*;
import org.json.JSONObject;

import java.util.Map;

public class StatsHandler implements RouteHandler {

    private final CrankerRouter router;

    public StatsHandler(CrankerRouter router) {
        this.router = router;
    }

    @Override
    public void handle(MuRequest req, MuResponse resp, Map<String, String> pathParams) {
        resp.contentType("application/json");
        MuStats stats = req.server().stats();
        JSONObject health = new JSONObject()
            .put("activeRequests", stats.activeConnections())
            .put("activeConnections", stats.activeRequests().size())
            .put("completedRequests", stats.completedRequests())
            .put("bytesSent", stats.bytesSent())
            .put("bytesReceived", stats.bytesRead())
            .put("invalidRequests", stats.invalidHttpRequests())
            .put("crankerVersion", CrankerRouter.muCrankerVersion())
            .put("services", router.collectInfo().toMap());
        resp.write(health.toString(2));
    }
}
