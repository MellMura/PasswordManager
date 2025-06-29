package application.managers;

import application.utils.EncryptionHandler;
import application.utils.JDBC_Handler;
import application.models.Account;
import application.models.UserSession;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {

    public static boolean saveAccount(int userId, int folderId, String name, String colorHex, String iconPath, String email, String password) {
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
                preparedStatement.setInt(2, folderId);
                preparedStatement.setString(3, name);
                preparedStatement.setString(4, email);
                preparedStatement.setString(5, encryptedPassword);
                preparedStatement.setString(6, iconPath);
                preparedStatement.setString(7, colorHex);
                preparedStatement.setInt(8, nextPosition);

                int rowsInserted = preparedStatement.executeUpdate();

                if (rowsInserted > 0) {
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
                String selectSql = "SELECT icon_url FROM saved_accounts WHERE name = ?";
                PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                selectStatement.setString(1, name);
                ResultSet rs = selectStatement.executeQuery();

                String iconPath = null;
                if (rs.next()) {
                    iconPath = rs.getString("icon_url");
                }

                String deleteSql = "DELETE FROM saved_accounts WHERE name = ?";
                PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
                deleteStatement.setString(1, name);
                int rowsDeleted = deleteStatement.executeUpdate();

                if (rowsDeleted > 0 && iconPath != null && !iconPath.isEmpty()) {
                    File iconFile = new File(iconPath);
                    if (iconFile.exists()) {
                        iconFile.delete();
                    }
                }

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

    public static boolean updateAccount(int id, String name, String colorHex, String iconPath, String email, String password) {
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
                String selectSql = "SELECT icon_url FROM saved_accounts WHERE name = ?";
                PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                selectStatement.setString(1, name);
                ResultSet rs = selectStatement.executeQuery();

                String icon = null;
                if (rs.next()) {
                    icon = rs.getString("icon_url");
                }

                String sql = "UPDATE saved_accounts SET name = ?, email = ?, password = ?, icon_url = ?, color = ? WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, encryptedPassword);
                preparedStatement.setString(4, iconPath);
                preparedStatement.setString(5, colorHex);
                preparedStatement.setInt(6, id);

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0 && icon != null && !icon.isEmpty()) {
                    File oldIcon = new File(icon);
                    if (oldIcon.exists()) {
                        oldIcon.delete();
                    }
                }
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("Database connection failed.");
            return false;
        }
    }

    public static boolean updateFolderId(int accountId, int folderId) {
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String sql = "UPDATE saved_accounts SET folder_id = ? WHERE id = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, folderId);
                preparedStatement.setInt(2, accountId);

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    return true;
                } else {
                    System.out.println("Folder id updating failed.");
                    return false;
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("Database connection failed.");
            return false;
        }
    }

    public List<Account> searchByName(String search) {
        List<Account> accounts = new ArrayList<>();
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String sql = "SELECT id, name, email, password, icon_url, color FROM saved_accounts WHERE user_id = ? AND name LIKE ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, UserSession.getUserId());
                preparedStatement.setString(2, "%" + search + "%");
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    try{
                        String decryptedPassword = EncryptionHandler.decrypt(resultSet.getString("password"));
                        accounts.add(new Account(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                resultSet.getString("email"),
                                decryptedPassword,
                                resultSet.getString("icon_url"),
                                resultSet.getString("color")
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

    public List<Account> fetchAccounts(int folderId) {
        List<Account> accounts = new ArrayList<>();
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String sql = "SELECT id, name, email, password, icon_url, color FROM saved_accounts WHERE user_id = ? AND folder_id = ? ORDER BY position ASC";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, UserSession.getUserId());
                preparedStatement.setInt(2, folderId);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    try{
                        String decryptedPassword = EncryptionHandler.decrypt(resultSet.getString("password"));
                        accounts.add(new Account(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                resultSet.getString("email"),
                                decryptedPassword,
                                resultSet.getString("icon_url"),
                                resultSet.getString("color")
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

    public static void reorderPositions(int fromPosition, int toPosition, int draggedId) {
        try (Connection conn = JDBC_Handler.connectDB()) {
            conn.setAutoCommit(false);

            if (fromPosition < toPosition) {
                PreparedStatement shiftUp = conn.prepareStatement(
                        "UPDATE saved_accounts SET position = position - 1 WHERE position > ? AND position <= ?"
                );
                shiftUp.setInt(1, fromPosition);
                shiftUp.setInt(2, toPosition);
                shiftUp.executeUpdate();
            } else if (fromPosition > toPosition) {
                PreparedStatement shiftDown = conn.prepareStatement(
                        "UPDATE saved_accounts SET position = position + 1 WHERE position >= ? AND position < ?"
                );
                shiftDown.setInt(1, toPosition);
                shiftDown.setInt(2, fromPosition);
                shiftDown.executeUpdate();
            }

            PreparedStatement updateDragged = conn.prepareStatement(
                    "UPDATE saved_accounts SET position = ? WHERE id = ?"
            );
            updateDragged.setInt(1, toPosition);
            updateDragged.setInt(2, draggedId);
            updateDragged.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}