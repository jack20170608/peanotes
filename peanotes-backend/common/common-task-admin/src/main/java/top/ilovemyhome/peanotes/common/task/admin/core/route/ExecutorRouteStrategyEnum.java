package top.ilovemyhome.peanotes.common.task.admin.core.route;


import top.ilovemyhome.peanotes.common.task.admin.core.route.strategy.*;

/**
 * Created by xuxueli on 17/3/10.
 */
public enum ExecutorRouteStrategyEnum {

    FIRST("First", new ExecutorRouteFirst()),
    LAST("Last", new ExecutorRouteLast()),
    ROUND("Round", new ExecutorRouteRound()),
    RANDOM("Random", new ExecutorRouteRandom()),
    CONSISTENT_HASH("ConsistentHash", new ExecutorRouteConsistentHash()),
    LEAST_FREQUENTLY_USED("LFU", new ExecutorRouteLFU()),
    LEAST_RECENTLY_USED("LRU", new ExecutorRouteLRU()),
    FAILOVER("FailOver", new ExecutorRouteFailover()),
    BUSYOVER("BusyOver", new ExecutorRouteBusyover()),
    SHARDING_BROADCAST("ShardingBroadcast", null);

    ExecutorRouteStrategyEnum(String title, ExecutorRouter router) {
        this.title = title;
        this.router = router;
    }

    private String title;
    private ExecutorRouter router;

    public String getTitle() {
        return title;
    }
    public ExecutorRouter getRouter() {
        return router;
    }

    public static ExecutorRouteStrategyEnum match(String name, ExecutorRouteStrategyEnum defaultItem){
        if (name != null) {
            for (ExecutorRouteStrategyEnum item: ExecutorRouteStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }

}
