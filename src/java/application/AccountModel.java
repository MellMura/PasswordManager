package application;

public class AccountModel {
    public int id;
    public final String name;
    public final String email;
    public final String password;
    public final String iconUrl;
    public final String color;

    public AccountModel(int id, String name, String email, String password, String iconUrl, String color) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.iconUrl = iconUrl;
        this.color = color;
    }
}
