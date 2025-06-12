package application;

public class UserSession {
    private static int userId;

    public static void setCurrentUserId(int id) {
        userId = id;
    }

    public static int getUserId() {
        return userId;
    }
}