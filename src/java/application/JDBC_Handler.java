package application;

import java.sql.*;

public class JDBC_Handler {
    public static Connection connectDB() {
        try {
            //create connection to the DB if the database already exists
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/password_manager",
                    "root",
                    "2509"
            );
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();

            return null;
        }
    }
}