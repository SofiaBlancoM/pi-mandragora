package es.cifpcarlos3.pimandragora.infrastructure.data.repositories.categories;


import com.fasterxml.jackson.core.type.TypeReference;
import es.cifpcarlos3.pimandragora.application.categories.repositories.CategoryQueryRepository;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.dtos.FindAllCategoriesResponse;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgrestApi;

import java.util.List;
import java.util.Map;

public record SupabaseCategoryQueryRepository(PostgrestApi postgrest) implements CategoryQueryRepository {
    private static final String TABLE = "categories";

    @Override
    public List<FindAllCategoriesResponse> findAll() {
        return postgrest.getList(
                TABLE,
                Map.of("select", "id,name", "order", "name.asc"),
                new TypeReference<>() {
                }
        );
    }
}
