package application;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class Login {
    public static boolean loginUser(String email, String password) {
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                String sql = "SELECT password FROM users WHERE email = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, email);

                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    return BCrypt.checkpw(password, storedHash);
                } else {
                    System.out.println("User not found.");
                    return false;
                }
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
