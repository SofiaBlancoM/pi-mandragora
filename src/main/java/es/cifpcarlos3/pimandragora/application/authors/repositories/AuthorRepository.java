package es.cifpcarlos3.pimandragora.application.authors.repositories;

import java.util.Map;

public interface AuthorRepository {
    void delete(String id);
    void save(String fullName);
    void update(String id, Map<String, Object> data);
}