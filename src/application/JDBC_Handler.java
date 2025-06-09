package application;

import java.sql.*;

public class JDBC_Handler {
    public static Connection connectDB() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/login_schema",
                    "root",
                    "2509"
            );

            //Statement statement = connection.createStatement();
            //ResultSet resultSet = statement.executeQuery("SELECT * FROM USERS");

            /*while (resultSet.next()) {
                System.out.println(resultSet.getString("username"));
                System.out.println(resultSet.getString("password"));
            }*/
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();

            return null;
        }
    }
}