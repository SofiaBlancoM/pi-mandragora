package es.cifpcarlos3.pimandragora.application.categories.usecases.deletecategory;

import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.categories.SupabaseCategoryQueryRepository;
import java.util.UUID;

public record DeleteCategoryUseCase(SupabaseCategoryQueryRepository repo) {
    public void execute(UUID id) {
        repo.delete(id);
    }
}
