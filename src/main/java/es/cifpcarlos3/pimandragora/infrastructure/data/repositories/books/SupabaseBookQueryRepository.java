package es.cifpcarlos3.pimandragora.infrastructure.data.repositories.books;

import com.fasterxml.jackson.core.type.TypeReference;
import es.cifpcarlos3.pimandragora.application.books.repositories.BookQueryRepository;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksListItemResponse;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksQuery;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgreClient;
import es.cifpcarlos3.pimandragora.shared.paging.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SupabaseBookQueryRepository(PostgreClient postgrest) implements BookQueryRepository {

    private static final Logger log =
            LoggerFactory.getLogger(SupabaseBookQueryRepository.class);

    private static final String TABLE = "books";

    @Override
    public Page<GetBooksListItemResponse> find(GetBooksQuery request) {

        String select = String.join(",",
                "id,isbn,title,author_id,category_id,publisher,publication_date,price,stock,status,cover_image_path,cover_image_updated_at,created_at,updated_at",
                "author:author_id(full_name)",
                "category:category_id(name)"
        );

        Map<String, String> query = getQueryMap(request, select);

        log.debug(
                "GET {} page={} size={} status={} searchText={} sort={} authorId={} categoryId={}",
                TABLE,
                request.page().page(),
                request.page().size(),
                request.status(),
                request.searchText(),
                request.sort(),
                request.authorId(),
                request.categoryId()
        );

        var page = postgrest.getPage(
                TABLE,
                query,
                request.page(),
                new TypeReference<List<BookListRow>>() {
                }
        );

        return new Page<>(
                page.items().stream().map(BookListRow::toResponse).toList(),
                page.page(),
                page.size(),
                page.totalItems()
        );
    }

    private static Map<String, String> getQueryMap(GetBooksQuery request, String select) {
        Map<String, String> query = new HashMap<>();
        query.put("select", select);
        query.put("order", orderBy(request.sort()));

        if (request.authorId() != null) query.put("author_id", "eq." + request.authorId());
        if (request.categoryId() != null) query.put("category_id", "eq." + request.categoryId());
        if (request.status() != null) query.put("status", "eq." + request.status());

        if (request.minPrice() != null) query.put("price", "gte." + request.minPrice());
        if (request.maxPrice() != null) query.put("price", "lte." + request.maxPrice());

        if (request.minStock() != null) query.put("stock", "gte." + request.minStock());
        if (request.maxStock() != null) query.put("stock", "lte." + request.maxStock());

        if (request.searchText() != null && !request.searchText().isBlank()) {
            String searchText = request.searchText().trim();
            query.put("or", "("
                    + "title.ilike.*" + searchText + "*,"
                    + "isbn.ilike.*" + searchText + "*,"
                    + "publisher.ilike.*" + searchText + "*"
                    + ")");
        }

        return query;
    }

    private static String orderBy(GetBooksQuery.Sort sort) {
        if (sort == null) return "created_at.desc";
        return switch (sort) {
            case TITLE_ASC -> "title.asc";
            case TITLE_DESC -> "title.desc";
            case PRICE_ASC -> "price.asc";
            case PRICE_DESC -> "price.desc";
            case PUBLICATION_DATE_DESC -> "publication_date.desc";
            case CREATED_AT_DESC -> "updated_at.desc";
        };
    }
}
