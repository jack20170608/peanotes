package top.ilovemyhome.peanotes.common.task.admin.core.executor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.admin.core.executor.JobExecutor;
import top.ilovemyhome.peanotes.common.task.admin.core.handler.annotation.XxlJob;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * xxl-job executor (for frameless)
 *
 * @author xuxueli 2020-11-05
 */
public class XxlJobSimpleExecutor extends JobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobSimpleExecutor.class);


    private List<Object> xxlJobBeanList = new ArrayList<>();
    public List<Object> getXxlJobBeanList() {
        return xxlJobBeanList;
    }
    public void setXxlJobBeanList(List<Object> xxlJobBeanList) {
        this.xxlJobBeanList = xxlJobBeanList;
    }


    @Override
    public void start() {

        // init JobHandler Repository (for method)
        initJobHandlerMethodRepository(xxlJobBeanList);

        // super start
        try {
            super.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }


    private void initJobHandlerMethodRepository(List<Object> xxlJobBeanList) {
        if (xxlJobBeanList==null || xxlJobBeanList.isEmpty()) {
            return;
        }

        // init job handler from method
        for (Object bean: xxlJobBeanList) {
            // method
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method executeMethod : methods) {
                XxlJob xxlJob = executeMethod.getAnnotation(XxlJob.class);
                // registry
                registJobHandler(xxlJob, bean, executeMethod);
            }

        }

    }

}
