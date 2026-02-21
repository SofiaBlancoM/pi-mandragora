package es.cifpcarlos3.pimandragora.infrastructure.data.repositories.authors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import es.cifpcarlos3.pimandragora.application.authors.repositories.AuthorQueryRepository;
import es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.AuthorDetailResponse;
import es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.FindAllAuthorsResponse;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgreClient; // 1. CAMBIADO
import es.cifpcarlos3.pimandragora.shared.paging.Page;
import es.cifpcarlos3.pimandragora.shared.paging.PageRequest;

import java.util.List;
import java.util.UUID;

import static java.util.Map.of;

// 2. CAMBIADO el parámetro a PostgreClient
public record SupabaseAuthorQueryRepository(PostgreClient postgrest) implements AuthorQueryRepository {

    private static final String TABLE = "authors";

    @Override
    public List<FindAllAuthorsResponse> findAll() {
        List<AuthorRow> rows = postgrest.getList(
                TABLE,
                of("select", "id,full_name,bio", "order", "full_name.asc"),
                new TypeReference<>() {}
        );
        return rows.stream().map(AuthorRow::toResponse).toList();
    }

    @Override
    public Page<FindAllAuthorsResponse> findPage(PageRequest pageRequest) {
        return postgrest.getPage(
                TABLE,
                of("select", "id,full_name,bio", "order", "full_name.asc"),
                pageRequest,
                new TypeReference<List<AuthorRow>>() {}
        ).map(AuthorRow::toResponse);
    }

    @Override
    public AuthorDetailResponse findById(String id) {

        List<AuthorDetailRow> rows = postgrest.getList(
                TABLE,
                of("select", "id,full_name,bio,books(id,title)", "id", "eq." + id),
                new TypeReference<>() {}
        );

        if (rows == null || rows.isEmpty()) return null;
        return rows.get(0).toResponse();
    }

    private record AuthorRow(
            String id,
            @JsonProperty("full_name") String fullName,
            String bio
    ) {
        public FindAllAuthorsResponse toResponse() {
            return new FindAllAuthorsResponse(UUID.fromString(id), fullName, bio);
        }
    }

    private record AuthorDetailRow(
            String id,
            @JsonProperty("full_name") String fullName,
            String bio,
            List<BookRow> books
    ) {
        public AuthorDetailResponse toResponse() {
            List<AuthorDetailResponse.AuthorBookResponse> bookDTOs = (books == null)
                    ? List.of()
                    : books.stream()
                    .map(b -> new AuthorDetailResponse.AuthorBookResponse(b.id, b.title, null))
                    .toList();

            return new AuthorDetailResponse(id, fullName, bio, bookDTOs);
        }
    }

    private record BookRow(
            String id,
            String title
    ) {}
}