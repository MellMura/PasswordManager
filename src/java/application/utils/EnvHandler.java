package application.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class EnvHandler {
    private static final HashMap<String, String> envMap = new HashMap<>();

    public static void loadEnv(String filePath) {
        try {
            Files.lines(Paths.get(filePath))
                    .filter(line -> line.contains("=") && !line.startsWith("#"))
                    .forEach(line -> {
                        String[] parts = line.split("=", 2);
                        envMap.put(parts[0].trim(), parts[1].trim());
                    });
        } catch (IOException e) {
            System.out.println("Failed to load .env file: " + filePath);
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return envMap.getOrDefault(key, "");
    }
}
