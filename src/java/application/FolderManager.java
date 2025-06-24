package application;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FolderManager {
    public static boolean saveFolder(int userId, String name) {
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String sql = "INSERT INTO folders (user_id, name) " +
                        "VALUES (?, ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, userId);
                preparedStatement.setString(2, name);

                int rowsInserted = preparedStatement.executeUpdate();

                if (rowsInserted > 0) {
                    System.out.println("Folder created successfully!");
                    return true;
                } else {
                    System.out.println("Folder creation failed.");
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

    public static boolean updateFolder(int id, String name) {
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String sql = "UPDATE folders SET name = ? WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, name);
                preparedStatement.setInt(2, id);

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    return true;
                } else {
                    System.out.println("Folder saving failed.");
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

    public List<FolderModel> fetchFolders() {
        List<FolderModel> folders = new ArrayList<>();
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String sql = "SELECT id, name FROM folders WHERE user_id = ? ORDER BY name ASC";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, UserSession.getUserId());
                ResultSet rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    folders.add(new FolderModel(
                            rs.getInt("id"),
                            rs.getString("name")
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Database connection failed.");
        }

        return folders;
    }
}
