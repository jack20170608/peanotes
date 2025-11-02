package top.ilovemyhome.issue.analysis.dbconnpool.common.operation;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.commons.common.io.ResourceUtil;
import top.ilovemyhome.issue.analysis.dbconnpool.common.BenchmarkTest;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Objects;

public class CreateTableOperation extends AbstractDbOperation{

    public CreateTableOperation(BenchmarkTest benchmarkTest) {
        super(benchmarkTest);
    }

    @Override
    public int execute(Connection connection, boolean closeConnection) {
        int result = 0;
        Objects.requireNonNull( connection, "Connection cannot be null");
        try (Statement stmt = connection.createStatement()) {
            String createTableSql = ResourceUtil.getClasspathResourceAsString("sql/createTable.sql");
            String[] createTableSqlList = StringUtils.split(createTableSql, ";");
            for(String sql : createTableSqlList){
                stmt.execute(sql);
            }
            logger.info("Test table initialized successfully");
            result = 1;
        } catch (Exception e) {
            logger.error("Error initializing test table: ", e);
        }finally {
            if(closeConnection){
                closeConnection(connection);
            }
        }
        return result;
    }


    private static final Logger logger = LoggerFactory.getLogger(CreateTableOperation.class);
}
