package es.cifpcarlos3.pimandragora.application.books.usecases.getbooks;


import es.cifpcarlos3.pimandragora.application.books.repositories.BookQueryRepository;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksListItemResponse;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksQuery;
import es.cifpcarlos3.pimandragora.shared.paging.Page;

import java.math.BigDecimal;

public record GetBooksUseCase(BookQueryRepository repository) {

    public GetBooksUseCase {
        if (repository == null) throw new IllegalArgumentException("repository is required");
    }

    public Page<GetBooksListItemResponse> execute(GetBooksQuery query) {
        if (query == null) throw new IllegalArgumentException("query is required");

        GetBooksQuery normalizedQuery = query.normalized();
        validate(normalizedQuery);

        return repository.find(normalizedQuery);
    }

    private static void validate(GetBooksQuery q) {
        // Price range
        if (q.minPrice() != null && q.maxPrice() != null) {
            if (q.minPrice().compareTo(q.maxPrice()) > 0) {
                throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
            }
        }

        // Non-negative prices
        if (q.minPrice() != null && isNegative(q.minPrice())) {
            throw new IllegalArgumentException("minPrice cannot be negative");
        }
        if (q.maxPrice() != null && isNegative(q.maxPrice())) {
            throw new IllegalArgumentException("maxPrice cannot be negative");
        }

        // Stock range
        if (q.minStock() != null && q.minStock() < 0) {
            throw new IllegalArgumentException("minStock cannot be negative");
        }
        if (q.maxStock() != null && q.maxStock() < 0) {
            throw new IllegalArgumentException("maxStock cannot be negative");
        }
        if (q.minStock() != null && q.maxStock() != null) {
            if (q.minStock() > q.maxStock()) {
                throw new IllegalArgumentException("minStock cannot be greater than maxStock");
            }
        }
    }

    private static boolean isNegative(BigDecimal v) {
        return v.signum() < 0;
    }
}
