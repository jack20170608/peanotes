package top.ilovemyhome.peanotes.common.task.exe.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.common.task.exe.common.FooTaskAdminServer;

public class StartAdminServerLocal {

    public static void main(String[] args) {
        FooTaskAdminServer adminServer = new FooTaskAdminServer(10086);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(StartAdminServerLocal.class);
}
