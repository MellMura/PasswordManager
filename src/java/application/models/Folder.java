package application.models;

public class Folder {
    public int id;
    public int folder_id;
    public String name;

    public Folder() {}

    public Folder(int id, int folder_id, String name) {
        this.id = id;
        this.folder_id = folder_id;
        this.name = name;
    }
}