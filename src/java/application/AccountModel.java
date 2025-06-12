package application;

public class AccountModel {
    public int id;
    public String name;
    public String email;
    public String password;
    public String iconUrl;
    public String color;

    public AccountModel() {}

    public AccountModel(int id, String name, String email, String password, String iconUrl, String color) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.iconUrl = iconUrl;
        this.color = color;
    }
}
