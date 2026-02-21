package es.cifpcarlos3.pimandragora.application.common.images;

import java.nio.file.Path;
import java.util.UUID;

/**
 * Clase para manejar fichero en el bucket de imágenes para los libros
 */
public interface BookCoverImageStorage {

    /**
     * Borra una imagen
     *
     * @param objectPath ruta de la imagen dentro del bucket
     */
    void deleteCover(String objectPath);

    /**
     * Sube una imagen al bucket
     *
     * @param bookId id del libro al que pertenece la imagen
     * @param file   imagen a subir
     * @return ruta de la imagen dentro del bucket
     */
    String uploadBookCover(UUID bookId, Path file);
}
