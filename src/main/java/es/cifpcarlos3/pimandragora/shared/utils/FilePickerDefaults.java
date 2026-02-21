package es.cifpcarlos3.pimandragora.shared.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utilidad multiplataforma para abrir un file picker en la carpeta de descargas
 */
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

        for (Path path : candidates) {
            if (isUsableDirectory(path)) return path.toFile();
        }
        return null;
    }

    private static boolean isUsableDirectory(Path path) {
        try {
            return path != null && Files.isDirectory(path) && Files.isReadable(path);
        } catch (Exception ignored) {
            return false;
        }
    }
}
