package es.cifpcarlos3.pimandragora.infrastructure.data.repositories.books;

import com.fasterxml.jackson.core.type.TypeReference;
import es.cifpcarlos3.pimandragora.application.books.repositories.BookQueryRepository;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksListItemResponse;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksQuery;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgrestApi;
import es.cifpcarlos3.pimandragora.shared.paging.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SupabaseBookQueryRepository(PostgrestApi postgrest) implements BookQueryRepository {

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
                "GET {} page={} size={} status={} text={} sort={} authorId={} categoryId={}",
                TABLE,
                request.page().page(),
                request.page().size(),
                request.status(),
                request.text(),
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

    private static Map<String, String> getQueryMap(GetBooksQuery q, String select) {
        Map<String, String> query = new HashMap<>();
        query.put("select", select);
        query.put("order", orderBy(q.sort()));

        if (q.authorId() != null) query.put("author_id", "eq." + q.authorId());
        if (q.categoryId() != null) query.put("category_id", "eq." + q.categoryId());
        if (q.status() != null) query.put("status", "eq." + q.status());

        if (q.minPrice() != null) query.put("price", "gte." + q.minPrice());
        if (q.maxPrice() != null) query.put("price", "lte." + q.maxPrice());

        if (q.minStock() != null) query.put("stock", "gte." + q.minStock());
        if (q.maxStock() != null) query.put("stock", "lte." + q.maxStock());

        if (q.text() != null && !q.text().isBlank()) {
            String t = q.text().trim();
            query.put("or", "("
                    + "title.ilike.*" + t + "*,"
                    + "isbn.ilike.*" + t + "*,"
                    + "publisher.ilike.*" + t + "*"
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
