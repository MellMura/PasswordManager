package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {

    public static boolean saveAccount(int userId, String name, String colorHex, String iconPath, String email, String password) {
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

                String posQuery = "SELECT MAX(position) AS max_pos FROM saved_accounts WHERE user_id = ?";
                PreparedStatement posStatement = connection.prepareStatement(posQuery);
                posStatement.setInt(1, userId);
                ResultSet rs = posStatement.executeQuery();

                int nextPosition = 1;
                if (rs.next()) {
                    nextPosition = rs.getInt("max_pos") + 1;
                }

                String sql = "INSERT INTO saved_accounts (user_id, folder_id, name, email, password, icon_url, color, position) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, 0); // default folder
                preparedStatement.setString(3, name);
                preparedStatement.setString(4, email);
                preparedStatement.setString(5, encryptedPassword);
                preparedStatement.setString(6, iconPath);
                preparedStatement.setString(7, colorHex);
                preparedStatement.setInt(8, nextPosition);

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

    public static boolean removeAccount(String name) {
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String sql = "DELETE FROM saved_accounts WHERE name = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, name);

                int rowsDeleted = preparedStatement.executeUpdate();

                return rowsDeleted > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("Database connection failed.");
            return false;
        }
    }

    public List<AccountModel> fetchAccounts() {
        List<AccountModel> accounts = new ArrayList<>();
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String sql = "SELECT name, email, password, icon_url, color FROM saved_accounts";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    try{
                        String decryptedPassword = EncryptionHandler.decrypt(rs.getString("password"));
                        accounts.add(new AccountModel(
                                rs.getString("name"),
                                rs.getString("email"),
                                decryptedPassword,
                                rs.getString("icon_url"),
                                rs.getString("color")
                        ));
                    } catch (Exception e){
                        e.printStackTrace();
                        System.out.println("Password decryption failed.");
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Database connection failed.");
        }

        return accounts;
    }
}