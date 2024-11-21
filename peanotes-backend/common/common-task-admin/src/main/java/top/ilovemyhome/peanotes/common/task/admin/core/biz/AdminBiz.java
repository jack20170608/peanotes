package top.ilovemyhome.peanotes.common.task.admin.core.biz;


import top.ilovemyhome.peanotes.common.task.admin.core.model.HandleCallbackParam;
import top.ilovemyhome.peanotes.common.task.admin.core.model.RegistryParam;
import top.ilovemyhome.peanotes.common.task.admin.core.model.ReturnT;

import java.util.List;

/**
 * @author xuxueli 2017-07-27 21:52:49
 */
public interface AdminBiz {


    // ---------------------- callback ----------------------

    /**
     * callback
     *
     * @param callbackParamList
     * @return
     */
    ReturnT<String> callback(List<HandleCallbackParam> callbackParamList);


    // ---------------------- registry ----------------------

    /**
     * registry
     *
     * @param registryParam
     * @return
     */
    ReturnT<String> registry(RegistryParam registryParam);

    /**
     * registry remove
     *
     * @param registryParam
     * @return
     */
    ReturnT<String> registryRemove(RegistryParam registryParam);


    // ---------------------- biz (custome) ----------------------
    // group、job ... manage

}
