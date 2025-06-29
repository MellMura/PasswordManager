package application.models;

public class UserSession {
    private static int userId;
    private static String username;

    public static void setCurrentUserId(int id) {
        userId = id;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUsername(String name) {
        username = name;
    }

    public static String getUsername() {
        return username;
    }
}