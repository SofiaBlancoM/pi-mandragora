package es.cifpcarlos3.pimandragora.infrastructure.data.repositories.users;

import es.cifpcarlos3.pimandragora.application.userprofile.repositories.UserProfileRepository;
import es.cifpcarlos3.pimandragora.domain.entities.User;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgrestApi;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record SupabaseUserProfileRepository(PostgrestApi postgrest) implements UserProfileRepository {

    public SupabaseUserProfileRepository {
        if (postgrest == null) throw new IllegalArgumentException("postgrest is required");
    }

    @Override
    public Optional<User> findById(UUID id) {
        if (id == null) throw new IllegalArgumentException("id is required");

        Map<String, String> q = new HashMap<>();
        q.put("id", "eq." + id);
        q.put("select", "id,username,display_name,role,created_at");

        SupabaseProfileRow row = postgrest.getSingleOrNull("profiles", q, SupabaseProfileRow.class);
        if (row == null) return Optional.empty();

        return Optional.of(toDomain(row));
    }

    private User toDomain(SupabaseProfileRow r) {
        Instant createdAt = r.createdAt();

        return User.builder()
                .id(r.id())
                .createdAt(createdAt)
                .updatedAt(null)
                .username(r.username())
                .email("-")
                .displayname(r.displayName())
                .role(defaultRole(r.role()))
                .build();
    }

    private String defaultRole(String role) {
        return (role == null || role.isBlank()) ? "USER" : role;
    }

    @Override
    public User save(User entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteById(UUID id) {
        throw new UnsupportedOperationException();
    }
}
