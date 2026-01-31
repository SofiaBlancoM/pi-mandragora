package es.cifpcarlos3.pimandragora.domain.common;

import java.util.Optional;
import java.util.UUID;

public interface Repository<T> {
    Optional<T> findById(UUID id);

    T save(T entity);

    void deleteById(UUID id);
}
