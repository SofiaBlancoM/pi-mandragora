package es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories;


import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.dtos.FindAllCategoriesResponse;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.categories.SupabaseCategoryQueryRepository;

import java.util.List;

public record FindAllCategoriesUseCase(SupabaseCategoryQueryRepository repo) {

    public List<FindAllCategoriesResponse> execute() {
        return repo.findAll();
    }
}
