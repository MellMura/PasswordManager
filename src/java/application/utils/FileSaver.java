package application.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileSaver {
    private static final String UPLOAD_DIR = "uploads";

    public static String saveToUploads(File sourceFile) {
        if (sourceFile == null) return null;

        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String extension = "";
            String originalName = sourceFile.getName();
            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex != -1) {
                extension = originalName.substring(dotIndex);
            }
            String newFileName = System.currentTimeMillis() + extension;

            File destFile = new File(uploadDir, newFileName);
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return UPLOAD_DIR + "/" + newFileName;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
