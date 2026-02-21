package es.cifpcarlos3.pimandragora.infrastructure.data.repositories.authors;

import com.fasterxml.jackson.core.type.TypeReference;
import es.cifpcarlos3.pimandragora.application.authors.repositories.AuthorRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgrestApi;

import java.util.Map;

public record SupabaseAuthorCommandRepository(PostgrestApi postgrest) implements AuthorRepository {

    private static final String TABLE = "authors";

    @Override
    public void delete(String id) {

        postgrest.delete(TABLE, Map.of("id", "eq." + id));
    }

    @Override
    public void save(String fullName) {

        postgrest.upsert(
                TABLE,
                Map.of("full_name", fullName),
                null,
                new TypeReference<>() {}
        );
    }

    @Override
    public void update(String id, Map<String, Object> data) {

        postgrest.patch(TABLE, Map.of("id", "eq." + id), data);
    }
}