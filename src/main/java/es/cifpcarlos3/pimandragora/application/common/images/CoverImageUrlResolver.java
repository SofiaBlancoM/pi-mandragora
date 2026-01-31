package es.cifpcarlos3.pimandragora.application.common.images;

public interface CoverImageUrlResolver {
    default void clear() {
    } // optional

    String resolve(String objectPath);
}
