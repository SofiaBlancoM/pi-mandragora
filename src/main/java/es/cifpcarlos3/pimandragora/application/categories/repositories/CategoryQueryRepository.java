package es.cifpcarlos3.pimandragora.application.categories.repositories;

import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.dtos.FindAllCategoriesResponse;

import java.util.List;

public interface CategoryQueryRepository {
    List<FindAllCategoriesResponse> findAll();
}
