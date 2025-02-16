package top.ilovemyhome.peanotes.backend.postgres;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class PostgresDBExample {
    public static void main(String[] args) {
        // URL to connect to a DuckDB database file (or in-memory database)
        String url = "jdbc:postgresql://10.10.10.5:5432/app";  // File-based database
        String username = "jack";
        String password = "1";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            // Create a statement object to execute SQL queries
            Statement stmt = conn.createStatement();

            // Create a table
            String createTableSQL = "CREATE TABLE students (id INTEGER, name VARCHAR, age INTEGER)";
            stmt.execute(createTableSQL);
            System.out.println("Table 'students' created.");

            log.info("Start..");
            // Insert data into the table
            for (int i = 0; i < 100000; i++) {
                String insertDataSQL = "INSERT INTO students VALUES (1, 'Alice', 20), (2, 'Bob', 22), (3, 'Charlie', 23)";
                stmt.execute(insertDataSQL);
            }
            log.info("Done..");
            //Query data size
            String selectSql = "select count(0) from students";
            ResultSet rs = stmt.executeQuery(selectSql);
            while (rs.next()) {
                log.info("data size is {}", rs.getInt(1));
            }
            rs.close();

            Thread.sleep(3600);
            // Query data from the table
//            String querySQL = "SELECT * FROM students";
//            ResultSet rs = stmt.executeQuery(querySQL);
//            log.info("Query results:");

            // Print the query results
//            while (rs.next()) {
//                int id = rs.getInt("id");
//                String name = rs.getString("name");
//                int age = rs.getInt("age");
//                System.out.printf("ID: %d, Name: %s, Age: %d%n", id, name, age);
//            }

            // Close the ResultSet
//            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(PostgresDBExample.class);
}
