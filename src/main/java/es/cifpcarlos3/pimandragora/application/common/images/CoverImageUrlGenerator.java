package es.cifpcarlos3.pimandragora.application.common.images;

/**
 * Genera urls para poder descargar/ver las imágenes descargadas en un bucket
 */
public interface CoverImageUrlGenerator {

    /**
     * Limpia la caché de las urls generadas
     */
    default void clear() {
    }

    /**
     * Genera una url de una imagen del bucket de imágenes de portada de los libros
     *
     * @param objectPath url de la imagen del libro
     * @return la url generada
     */
    String generate(String objectPath);
}
