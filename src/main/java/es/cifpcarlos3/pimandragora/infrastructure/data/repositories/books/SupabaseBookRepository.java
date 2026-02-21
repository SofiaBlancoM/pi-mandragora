package es.cifpcarlos3.pimandragora.infrastructure.data.repositories.books;

import com.fasterxml.jackson.core.type.TypeReference;
import es.cifpcarlos3.pimandragora.application.books.repositories.BookRepository;
import es.cifpcarlos3.pimandragora.domain.entities.Book;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgreClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Map.of;

public record SupabaseBookRepository(PostgreClient postgrest) implements BookRepository {

    private static final String TABLE = "books";

    @Override
    public void deleteById(UUID id) {
        postgrest.delete(TABLE, of("id", "eq." + id));
    }

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
            throw new RuntimeException("La inserción a supabase no devolvió ninguna fila para el libro con el id: " + entity.getId());
        }

        return result.getFirst().toDomain();
    }
}
