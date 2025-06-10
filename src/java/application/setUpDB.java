package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class setUpDB {
    public static void initializeDB() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/", "root", "2509");
            Statement statement = connection.createStatement();

            //create DB if not exists
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS password_manager");
            System.out.println("Database 'password_manager' ensured.");

            //connect to the new DB
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/password_manager", "root", "2509");

            //create users table if not exists
            statement = connection.createStatement();
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT, " +
                            "email VARCHAR(100) NOT NULL UNIQUE, " +
                            "password VARCHAR(80) NOT NULL," +
                            "username VARCHAR(60) NOT NULL" +
                            ")"
            );

            System.out.println("Table 'users' ensured.");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
