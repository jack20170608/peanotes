package top.ilovemyhome.peanotes.common.task.admin.core.route.strategy;


import top.ilovemyhome.peanotes.common.task.admin.core.biz.ExecutorBiz;
import top.ilovemyhome.peanotes.common.task.admin.core.model.ReturnT;
import top.ilovemyhome.peanotes.common.task.admin.core.model.TriggerParam;
import top.ilovemyhome.peanotes.common.task.admin.core.route.ExecutorRouter;
import top.ilovemyhome.peanotes.common.task.admin.schedule.JobScheduler;

import java.util.List;

/**
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteFailover extends ExecutorRouter {

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {

        StringBuffer beatResultSB = new StringBuffer();
        for (String address : addressList) {
            // beat
            ReturnT<String> beatResult = null;
            try {
                ExecutorBiz executorBiz = JobScheduler.getInstance().getExecutorBiz(address);
                beatResult = executorBiz.beat();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                beatResult = new ReturnT<>(ReturnT.FAIL_CODE, "" + e);
            }
            beatResultSB.append((beatResultSB.length() > 0) ? "<br><br>" : "")
                .append("Heartbeats:")
                .append("<br>address：").append(address)
                .append("<br>code：").append(beatResult.getCode())
                .append("<br>msg：").append(beatResult.getMsg());

            // beat success
            if (beatResult.getCode() == ReturnT.SUCCESS_CODE) {

                beatResult.setMsg(beatResultSB.toString());
                beatResult.setContent(address);
                return beatResult;
            }
        }
        return new ReturnT<>(ReturnT.FAIL_CODE, beatResultSB.toString());

    }
}
