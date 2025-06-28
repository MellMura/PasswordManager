package application;

public class FolderModel {
    public int id;
    public int folder_id;
    public String name;

    public FolderModel() {}

    public FolderModel(int id, int folder_id, String name) {
        this.id = id;
        this.folder_id = folder_id;
        this.name = name;
    }
}