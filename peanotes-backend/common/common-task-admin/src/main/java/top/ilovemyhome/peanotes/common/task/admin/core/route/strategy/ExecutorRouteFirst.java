package top.ilovemyhome.peanotes.common.task.admin.core.route.strategy;


import top.ilovemyhome.peanotes.common.task.admin.core.model.ReturnT;
import top.ilovemyhome.peanotes.common.task.admin.core.model.TriggerParam;
import top.ilovemyhome.peanotes.common.task.admin.core.route.ExecutorRouter;

import java.util.List;

/**
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteFirst extends ExecutorRouter {

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList){
        return new ReturnT<String>(addressList.get(0));
    }

}
