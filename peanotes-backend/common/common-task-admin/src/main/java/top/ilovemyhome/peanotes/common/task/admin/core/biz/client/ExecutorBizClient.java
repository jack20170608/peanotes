package top.ilovemyhome.peanotes.common.task.admin.core.biz.client;


import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.common.task.admin.core.biz.ExecutorBiz;
import top.ilovemyhome.peanotes.common.task.admin.core.http.HttpUtils;
import top.ilovemyhome.peanotes.common.task.admin.core.model.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.LogRecord;

/**
 * admin api test
 *
 * @author xuxueli 2017-07-28 22:14:52
 */
public class ExecutorBizClient implements ExecutorBiz {

    public ExecutorBizClient() {
    }

    public ExecutorBizClient(String addressUrl, String accessToken) {
        this.addressUrl = addressUrl;
        this.accessToken = accessToken;

        // valid
        if (!this.addressUrl.endsWith("/")) {
            this.addressUrl = this.addressUrl + "/";
        }
    }

    private String addressUrl;
    private String accessToken;
    private int timeout = 3;


    @Override
    public ReturnT<String> beat() {
        return HttpUtils.post(addressUrl + "beat", timeout, Map.of("token", accessToken), "");
    }

    @Override
    public ReturnT<String> idleBeat(IdleBeatParam idleBeatParam) {
        return HttpUtils.post(addressUrl + "idleBeat", timeout, Map.of("token", accessToken), JacksonUtil.toJson(idleBeatParam));
    }

    @Override
    public ReturnT<String> run(TriggerParam triggerParam) {
        return HttpUtils.post(addressUrl + "run", timeout, Map.of("token", accessToken), JacksonUtil.toJson(triggerParam));
    }

    @Override
    public ReturnT<String> kill(KillParam killParam) {
        return HttpUtils.post(addressUrl + "kill", timeout, Map.of("token", accessToken), JacksonUtil.toJson(killParam));
    }

    @Override
    public ReturnT<LogResult> log(LogParam logParam) {
        ReturnT<LogResult> result;
        ReturnT<String> r = HttpUtils.post(addressUrl + "log", timeout, Map.of("token", accessToken), JacksonUtil.toJson(logParam));
        result = new ReturnT<LogResult>(r.getCode(), null);
        if (r.getCode() == ReturnT.SUCCESS_CODE){
            result.setContent(JacksonUtil.fromJson(r.getContent(), LogResult.class));
        }else {
            result.setMsg(r.getMsg());
        }
        return result;
    }

}
