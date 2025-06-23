package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
