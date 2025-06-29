package application.managers.auth;

import application.utils.JDBC_Handler;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class RegisterManager {
    public static boolean registerUser(String username, String email, String password) {
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(1, username);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, hashedPassword);

                int rowsInserted = preparedStatement.executeUpdate();

                if (rowsInserted > 0) {
                    System.out.println("User registered successfully!");
                    return true;
                } else {
                    System.out.println("User registration failed.");
                    return false;
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                System.out.println("Error: That email is already registered.");
                return false;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("Database connection failed.");
            return false;
        }
    }
}