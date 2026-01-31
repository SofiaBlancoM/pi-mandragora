package es.cifpcarlos3.pimandragora.infrastructure.data.repositories.authors;

import com.fasterxml.jackson.core.type.TypeReference;
import es.cifpcarlos3.pimandragora.application.authors.repositories.AuthorQueryRepository;
import es.cifpcarlos3.pimandragora.application.authors.usecases.findallauthors.dtos.FindAllAuthorsResponse;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgrestApi;

import java.util.List;

import static java.util.Map.of;

public record SupabaseAuthorQueryRepository(PostgrestApi postgrest) implements AuthorQueryRepository {

    private static final String TABLE = "authors";

    @Override
    public List<FindAllAuthorsResponse> findAll() {
        // keep payload small
        List<AuthorRow> rows = postgrest.getList(
                TABLE,
                of("select", "id,full_name", "order", "full_name.asc"),
                new TypeReference<>() {
                }
        );

        return rows.stream().map(AuthorRow::toResponse).toList();
    }
}
