package es.cifpcarlos3.pimandragora.domain.common;

import java.util.Optional;
import java.util.UUID;

/**
 * Patrón repository que define las llamadas principales a base de datos
 *
 * @param <T>
 */
public interface Repository<T> {

    /**
     * Borra la entidad por el ID
     *
     * @param id id de la entidad
     */
    void deleteById(UUID id);

    /**
     * Obtiene una entidad por el ID
     *
     * @param id id de la entidad
     * @return devuelve la entidad
     */
    Optional<T> findById(UUID id);

    /**
     * Crea o actualiza si ya existe una entidad en base de datos
     *
     * @param entity entidad en base de datos
     * @return devuelve la entidad guardada
     */
    T save(T entity);
}
