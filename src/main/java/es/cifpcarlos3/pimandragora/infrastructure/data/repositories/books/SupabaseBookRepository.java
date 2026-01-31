package es.cifpcarlos3.pimandragora.infrastructure.data.repositories.books;

import com.fasterxml.jackson.core.type.TypeReference;
import es.cifpcarlos3.pimandragora.application.books.repositories.BookRepository;
import es.cifpcarlos3.pimandragora.domain.entities.Book;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgrestApi;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Map.of;

public record SupabaseBookRepository(PostgrestApi postgrest) implements BookRepository {

    private static final String TABLE = "books";

    @Override
    public Optional<Book> findById(UUID id) {
        List<BookRow> rows = postgrest.getList(
                TABLE,
                of("select", "*", "id", "eq." + id),
                new TypeReference<>() {
                }
        );

        if (rows.isEmpty()) return Optional.empty();
        return Optional.of(rows.getFirst().toDomain());
    }

    @Override
    public Book save(Book entity) {
        BookRow row = BookRow.from(entity);

        List<BookRow> result = postgrest.upsert(
                TABLE,
                row,
                of("on_conflict", "id"),
                new TypeReference<>() {
                }
        );

        if (result.isEmpty()) {
            throw new RuntimeException("Supabase upsert returned no rows for book " + entity.getId());
        }

        return result.getFirst().toDomain();
    }

    @Override
    public void deleteById(UUID id) {
        postgrest.delete(TABLE, of("id", "eq." + id));
    }
}
