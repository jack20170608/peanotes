package top.ilovemyhome.peanotes.common.task.exe.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.exe.domain.ITask;

import java.util.concurrent.TimeUnit;


public class SimpleTask {

    private static Logger LOGGER = LoggerFactory.getLogger(SimpleTask.class);

    @ITask("helloWorld")
    public void helloWorld() {
        LOGGER.info("Hello World!");
    }

    @ITask("simpleHandler")
    public void simpleHandler() throws Exception {
        LOGGER.info("Task, Hello World.");
        for (int i = 0; i < 5; i++) {
            LOGGER.info("beat at:" + i);
            TimeUnit.SECONDS.sleep(2);
        }
    }

    @ITask(value = "lifeCycleHandler", init = "init", destroy = "destroy")
    public void lifeCycleHandler() throws Exception {
        LOGGER.info("XXL-JOB, Hello World.");
    }

    public void init(){
        LOGGER.info("init");
    }
    public void destroy(){
        LOGGER.info("destroy");
    }


}
