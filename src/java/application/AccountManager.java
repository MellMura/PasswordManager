package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AccountManager {
    public static boolean saveAccount(String name, String colorHex, String icon_path, String email, String password) {
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String encryptedPassword;
                try {
                    encryptedPassword = EncryptionHandler.encrypt(password);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Password encryption failed.");
                    return false;
                }

                String sql = "INSERT INTO saved_accounts (name, email, password, icon_url, color) VALUES (?, ?, ?, ?, ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(1, name);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, encryptedPassword);
                preparedStatement.setString(4, icon_path);
                preparedStatement.setString(5, colorHex);

                int rowsInserted = preparedStatement.executeUpdate();

                if (rowsInserted > 0) {
                    System.out.println("Account saved successfully!");
                    return true;
                } else {
                    System.out.println("Account saving failed.");
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