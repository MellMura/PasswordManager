package application;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

public class SessionHandler {
    private static final String SESSION_FILE = System.getProperty("user.home") + File.separator + ".session_token";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String generateToken() {
        return UUID.randomUUID().toString() + "-" + Base64.getUrlEncoder().encodeToString(new byte[16]);
    }

    public static void saveSession(int userId, String email, String token) {
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(3);

        try (Connection conn = JDBC_Handler.connectDB()) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO session_tokens (user_id, token, expires_at) VALUES (?, ?, ?)"
            );
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, token);
            preparedStatement.setString(3, expiresAt.format(formatter));
            preparedStatement.executeUpdate();

            Path path = Paths.get(System.getProperty("user.home"), ".password_manager", "last_login.txt");
            Files.writeString(path, email);
            Files.write(Path.of(SESSION_FILE), token.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Integer validateSessionToken() {
        try {
            String token = Files.readString(Path.of(SESSION_FILE)).trim();

            try (Connection conn = JDBC_Handler.connectDB()) {
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT user_id FROM session_tokens WHERE token = ? AND expires_at > NOW()"
                );
                stmt.setString(1, token);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearSession() {
        try {
            String token = Files.readString(Path.of(SESSION_FILE)).trim();

            try (Connection conn = JDBC_Handler.connectDB()) {
                PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM session_tokens WHERE token = ?"
                );
                stmt.setString(1, token);
                stmt.executeUpdate();
            }

            Files.deleteIfExists(Path.of(SESSION_FILE));
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static String loadEmail() {
        try {
            Path path = Paths.get(System.getProperty("user.home"), ".password_manager", "last_login.txt");
            if (Files.exists(path)) {
                return Files.readString(path).trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void saveEmail(String email) {
        try {
            Path path = Paths.get(System.getProperty("user.home"), ".password_manager");
            Files.createDirectories(path);
            Files.writeString(path.resolve("last_login.txt"), email);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
