package es.cifpcarlos3.pimandragora.infrastructure.data.repositories.categories;

import com.fasterxml.jackson.core.type.TypeReference;
import es.cifpcarlos3.pimandragora.application.categories.repositories.CategoryQueryRepository;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.dtos.FindAllCategoriesResponse;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgreClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record SupabaseCategoryQueryRepository(PostgreClient postgrest) implements CategoryQueryRepository {
    private static final String TABLE = "categories";

    public void delete(UUID id) {
        postgrest.delete(TABLE, Map.of("id", "eq." + id));
    }

    @Override
    public List<FindAllCategoriesResponse> findAll() {
        return postgrest.getList(
                TABLE,
                Map.of("select", "id,name", "order", "name.asc"),
                new TypeReference<>() {
                }
        );
    }

    public void save(String name) {
        postgrest.upsert(
                TABLE,
                new CategoryCreateRequest(name),
                Map.of(),
                new TypeReference<List<FindAllCategoriesResponse>>() {
                }
        );
    }

    public void update(UUID id, String name) {
        postgrest.upsert(
                TABLE,
                new CategoryUpdateRequest(id, name),
                Map.of("id", "eq." + id),
                new TypeReference<List<FindAllCategoriesResponse>>() {
                }
        );
    }
}

record CategoryCreateRequest(String name) {
}

record CategoryUpdateRequest(UUID id, String name) {
}

