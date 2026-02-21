package es.cifpcarlos3.pimandragora.presentation.app.config;

import es.cifpcarlos3.pimandragora.presentation.app.constants.ConfigConstants;
import es.cifpcarlos3.pimandragora.shared.utils.url.BaseUrlNormalizer;

import java.io.InputStream;
import java.util.Properties;

/**
 * Clase para recuperar las propiedades del local.properties y de las variables de entorno
 */
public final class AppConfig {

    private static final Properties PROPS = new Properties();

    static {
        load();
    }

    public static int getInt(PropertyKey key) {
        String v = getProperty(key);
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Invalid int for " + key + ": " + v, ex);
        }
    }

    public static String getProperty(PropertyKey key) {
        return PROPS.getProperty(key.propertyKey);
    }

    public static int getInt(PropertyKey key, int defaultValue) {
        String raw = PROPS.getProperty(key.propertyKey);
        if (raw == null || raw.isBlank()) return defaultValue;

        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException(
                    "Invalid int for property " + key.propertyKey + ": " + raw, ex
            );
        }
    }

    public static String supabaseUrl() {
        return BaseUrlNormalizer.normalize(getProperty(PropertyKey.SUPABASE_URL));
    }

    private static void load() {
        try {
            var url = AppConfig.class.getResource(ConfigConstants.PROPERTIES_FILE_PATH);
            if (url != null) {
                try (InputStream in = url.openStream()) {
                    PROPS.load(in);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + ConfigConstants.PROPERTIES_FILE_PATH, e);

        }

        for (PropertyKey key : PropertyKey.values()) {
            overrideFromEnv(key);
            if (key.required) require(key);
        }
    }

    private static void overrideFromEnv(PropertyKey key) {
        String value = System.getenv(key.envKey);
        if (value != null && !value.isBlank()) {
            PROPS.setProperty(key.propertyKey, value);
        }
    }

    private static void require(PropertyKey key) {
        String value = PROPS.getProperty(key.propertyKey);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing required config property: " + key.propertyKey
            );
        }
    }

}
