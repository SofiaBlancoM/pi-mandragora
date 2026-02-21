package es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories;


import es.cifpcarlos3.pimandragora.application.categories.repositories.CategoryQueryRepository;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.dtos.FindAllCategoriesResponse;

import java.util.List;

public record FindAllCategoriesUseCase(CategoryQueryRepository repo) {

    public List<FindAllCategoriesResponse> execute() {
        return repo.findAll();
    }
}
