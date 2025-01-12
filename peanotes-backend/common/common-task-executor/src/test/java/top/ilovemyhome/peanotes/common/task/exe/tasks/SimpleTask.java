package top.ilovemyhome.peanotes.common.task.exe.tasks;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.exe.TaskContext;
import top.ilovemyhome.peanotes.common.task.exe.domain.ITask;

import java.util.concurrent.TimeUnit;

import static top.ilovemyhome.peanotes.common.task.exe.TaskExecutor.CONTEXT;


public class SimpleTask {

    private static Logger LOGGER = LoggerFactory.getLogger(SimpleTask.class);

    @ITask("helloWorld")
    public void helloWorld() {
        TaskContext taskContext = CONTEXT.get();
        FileAppender<ILoggingEvent> fileAppender = taskContext.getLogFileAppender();
        ((ch.qos.logback.classic.Logger) LOGGER).addAppender(fileAppender);
        try {
            LOGGER.info("Hello World!");
        }catch (Throwable throwable){
            assert true;
        }finally {
            ((ch.qos.logback.classic.Logger) LOGGER).detachAppender(fileAppender);
        }
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
