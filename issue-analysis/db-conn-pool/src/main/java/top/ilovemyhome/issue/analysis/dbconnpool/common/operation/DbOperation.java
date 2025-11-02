package top.ilovemyhome.issue.analysis.dbconnpool.common.operation;

import java.sql.Connection;

public interface DbOperation {

    int execute(Connection connection, boolean closeConnection);

    default void closeConnection(Connection connection){
        if(connection != null){
            try {
                connection.close();
            } catch (Exception ignore) {
            }
        }
    }

}
