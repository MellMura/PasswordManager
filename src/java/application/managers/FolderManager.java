package application.managers;

import application.models.Folder;
import application.utils.JDBC_Handler;
import application.models.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FolderManager {

    public static Folder getFolderById(int id) {
        Connection connection = JDBC_Handler.connectDB();
        if (connection != null) {
            try {
                String sql = "SELECT id, folder_id, name FROM folders WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return new Folder(resultSet.getInt("id"), resultSet.getInt("folder_id"), resultSet.getString("name"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean saveFolder(int userId, int folderId, String name) {
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String sql = "INSERT INTO folders (user_id, folder_id, name) " +
                        "VALUES (?, ?, ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, folderId);
                preparedStatement.setString(3, name);

                int rowsInserted = preparedStatement.executeUpdate();

                if (rowsInserted > 0) {
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

    public static boolean removeFolder(String name) {
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String deleteSql = "DELETE FROM folders WHERE name = ?";
                PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
                deleteStatement.setString(1, name);
                int rowsDeleted = deleteStatement.executeUpdate();

                if (rowsDeleted > 0) {
                    return true;
                }
                else {
                    System.out.println("Folder deletion failed.");
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

    public List<Folder> searchByName(String search) {
        List<Folder> folders = new ArrayList<>();
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String sql = "SELECT id, folder_id, name FROM folders WHERE user_id = ? AND name LIKE ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, UserSession.getUserId());
                preparedStatement.setString(2, "%" + search + "%");
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    folders.add(new Folder(
                            resultSet.getInt("id"),
                            resultSet.getInt("folder_id"),
                            resultSet.getString("name")
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

    public List<Folder> fetchFolders(int folderId) {
        List<Folder> folders = new ArrayList<>();
        Connection connection = JDBC_Handler.connectDB();

        if (connection != null) {
            try {
                String sql = "SELECT id, folder_id, name FROM folders WHERE user_id = ? AND folder_id = ? ORDER BY name ASC";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, UserSession.getUserId());
                preparedStatement.setInt(2, folderId);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    folders.add(new Folder(
                            resultSet.getInt("id"),
                            resultSet.getInt("folder_id"),
                            resultSet.getString("name")
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
