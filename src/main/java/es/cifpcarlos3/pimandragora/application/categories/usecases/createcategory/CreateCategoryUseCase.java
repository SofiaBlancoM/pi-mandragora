package es.cifpcarlos3.pimandragora.application.categories.usecases.createcategory;

import es.cifpcarlos3.pimandragora.application.categories.usecases.createcategory.dtos.CreateCategoryRequest;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.categories.SupabaseCategoryQueryRepository;

public record CreateCategoryUseCase(SupabaseCategoryQueryRepository repo) {
    public void execute(CreateCategoryRequest request) {
        repo.save(request.name());
    }
}