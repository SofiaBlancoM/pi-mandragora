package es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos;

import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;
import es.cifpcarlos3.pimandragora.shared.paging.PageRequest;

import java.math.BigDecimal;
import java.util.UUID;

public record GetBooksQuery(
        String text,
        UUID authorId,
        UUID categoryId,
        BookStatus status,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer minStock,
        Integer maxStock,
        Sort sort,
        PageRequest page
) {
    private static String normalizeText(String t) {
        if (t == null) return null;
        String x = t.trim();
        return x.isBlank() ? null : x;
    }

    public GetBooksQuery normalized() {
        return new GetBooksQuery(
                normalizeText(text),
                authorId,
                categoryId,
                status,
                minPrice,
                maxPrice,
                minStock,
                maxStock,
                (sort == null) ? Sort.CREATED_AT_DESC : sort,
                (page == null) ? PageRequest.firstPage(25) : page
        );
    }

    public enum Sort {
        TITLE_ASC,
        TITLE_DESC,
        PRICE_ASC,
        PRICE_DESC,
        PUBLICATION_DATE_DESC,
        CREATED_AT_DESC
    }
}
