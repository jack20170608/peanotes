package top.ilovemyhome.peanotes.common.task.admin.core.biz.client;


import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.common.task.admin.core.biz.AdminBiz;
import top.ilovemyhome.peanotes.common.task.admin.core.http.HttpUtils;
import top.ilovemyhome.peanotes.common.task.admin.core.model.HandleCallbackParam;
import top.ilovemyhome.peanotes.common.task.admin.core.model.RegistryParam;
import top.ilovemyhome.peanotes.common.task.admin.core.model.ReturnT;

import java.util.List;
import java.util.Map;

/**
 * admin api test
 *
 * @author xuxueli 2017-07-28 22:14:52
 */
public class AdminBizClient implements AdminBiz {

    public AdminBizClient() {
    }
    public AdminBizClient(String addressUrl, String accessToken) {
        this.addressUrl = addressUrl;
        this.accessToken = accessToken;

        // valid
        if (!this.addressUrl.endsWith("/")) {
            this.addressUrl = this.addressUrl + "/";
        }
    }

    private String addressUrl ;
    private String accessToken;
    private int timeout = 3;


    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        return HttpUtils.post(addressUrl + "api/callback", timeout, Map.of("token", accessToken), JacksonUtil.toJson(callbackParamList));
    }

    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        return HttpUtils.post(addressUrl + "api/registry", timeout, Map.of("token", accessToken), JacksonUtil.toJson(registryParam));
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        return HttpUtils.post(addressUrl + "api/registryRemove", timeout, Map.of("token", accessToken), JacksonUtil.toJson(registryParam));
    }

}
