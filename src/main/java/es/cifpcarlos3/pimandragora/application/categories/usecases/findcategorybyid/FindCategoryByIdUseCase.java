package es.cifpcarlos3.pimandragora.application.categories.usecases.findcategorybyid;

import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.dtos.FindAllCategoriesResponse;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.categories.SupabaseCategoryQueryRepository;
import java.util.UUID;

public record FindCategoryByIdUseCase(SupabaseCategoryQueryRepository repo) {
    public FindAllCategoriesResponse execute(UUID id) {
        return repo.findAll().stream()
                .filter(c -> c.id().equals(id))
                .findFirst()
                .orElse(null);
    }
}
