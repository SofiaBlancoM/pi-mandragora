package es.cifpcarlos3.pimandragora.shared.utils.url;

public final class BaseUrlNormalizer {

    public static String normalize(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL must not be null or blank");
        }

        return url.endsWith("/")
                ? url.substring(0, url.length() - 1)
                : url;
    }
}
