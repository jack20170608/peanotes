package top.ilovemyhome.issue.analysis.dbconnpool.common;

import java.sql.Connection;
import java.util.function.Supplier;

public interface BenchmarkTest {

    void initSchema();

    void initData();

    void start();

    void stop();

    void cleanup();

    Supplier<Connection> getConnectionSupplier();

    int getThreadCount();

    boolean isAutoCommit();

    BenchmarkMonitor getMonitor();
}
