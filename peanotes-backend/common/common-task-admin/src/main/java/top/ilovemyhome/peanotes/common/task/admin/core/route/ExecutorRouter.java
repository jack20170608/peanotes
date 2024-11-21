package top.ilovemyhome.peanotes.common.task.admin.core.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.admin.core.model.ReturnT;
import top.ilovemyhome.peanotes.common.task.admin.core.model.TriggerParam;
import top.ilovemyhome.peanotes.common.task.admin.web.handlers.helper.R;

import java.util.List;

/**
 * Created by xuxueli on 17/3/10.
 */
public abstract class ExecutorRouter {
    protected static Logger logger = LoggerFactory.getLogger(ExecutorRouter.class);

    /**
     * route address
     *
     * @param addressList
     * @return  ReturnT.content=address
     */
    public abstract ReturnT<String> route(TriggerParam triggerParam, List<String> addressList);

}
