package es.cifpcarlos3.pimandragora.shared.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FilePickerDefaults {
    public static File defaultInitialDirectory() {
        String home = System.getProperty("user.home");
        if (home == null || home.isBlank()) return null;

        Path[] candidates = {
                Paths.get(home, "Downloads"),
                Paths.get(home, "OneDrive", "Downloads"),
                Paths.get(home, "OneDrive - Personal", "Downloads"),
                Paths.get(home)
        };

        for (Path p : candidates) {
            if (isUsableDirectory(p)) return p.toFile();
        }
        return null;
    }

    private static boolean isUsableDirectory(Path p) {
        try {
            return p != null && Files.isDirectory(p) && Files.isReadable(p);
        } catch (Exception ignored) {
            return false;
        }
    }
}
