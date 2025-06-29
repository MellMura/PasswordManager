package application.managers.auth;

import application.utils.JDBC_Handler;
import application.models.UserSession;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class LoginManager {
    public static Integer loginUser(String email, String password) {
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                String sql = "SELECT id, username, password FROM users WHERE email = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, email);

                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    UserSession.setUsername(rs.getString("username"));
                    if (BCrypt.checkpw(password, storedHash)) {
                        return rs.getInt("id");
                    }
                } else {
                    System.out.println("User not found.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Database connection failed.");
        }
        return null;
    }
}
