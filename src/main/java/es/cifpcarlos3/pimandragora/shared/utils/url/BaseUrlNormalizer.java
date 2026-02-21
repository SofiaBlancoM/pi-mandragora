package es.cifpcarlos3.pimandragora.shared.utils.url;

/**
 * Borra la barra final en una url
 */
public final class BaseUrlNormalizer {

    public static String normalize(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("La url no puede ser nula o estar en blanco");
        }

        return url.endsWith("/")
                ? url.substring(0, url.length() - 1)
                : url;
    }
}
