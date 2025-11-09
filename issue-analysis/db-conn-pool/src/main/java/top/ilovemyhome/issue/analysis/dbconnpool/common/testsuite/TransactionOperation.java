package top.ilovemyhome.issue.analysis.dbconnpool.common.testsuite;


import java.sql.Connection;

public interface TransactionOperation {

    void execute(Connection connection, boolean closeConnection);

    default void closeConnection(Connection connection){
        if(connection != null){
            try {
                connection.close();
            } catch (Exception ignore) {
            }
        }
    }

    default void close(AutoCloseable... resources) {
        for (AutoCloseable res : resources) {
            if (res != null) {
                try {
                    res.close();
                } catch (Exception ignore) {
                }
            }
        }
    }
}
