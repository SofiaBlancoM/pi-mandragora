package es.cifpcarlos3.pimandragora.application.categories.repositories;

import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.dtos.FindAllCategoriesResponse;

import java.util.List;

/**
 * Repositorio de solo lectura para las categorías
 */
public interface CategoryQueryRepository {
    List<FindAllCategoriesResponse> findAll();
}
