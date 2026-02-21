package es.cifpcarlos3.pimandragora.infrastructure.data.repositories.users;

import es.cifpcarlos3.pimandragora.application.userprofile.repositories.UserProfileRepository;
import es.cifpcarlos3.pimandragora.domain.entities.User;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgreClient;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record SupabaseUserProfileRepository(PostgreClient postgreClient) implements UserProfileRepository {

    @Override
    public void deleteById(UUID id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<User> findById(UUID id) {
        if (id == null) throw new IllegalArgumentException("El id es obligatorio");

        Map<String, String> query = new HashMap<>();
        query.put("id", "eq." + id);
        query.put("select", "id,username,display_name,role,created_at");

        SupabaseProfileRow row = postgreClient.getSingleOrNull("profiles", query, SupabaseProfileRow.class);
        if (row == null) return Optional.empty();

        return Optional.of(toDomain(row));
    }

    private User toDomain(SupabaseProfileRow row) {
        Instant createdAt = row.createdAt();

        return User.builder()
                .id(row.id())
                .createdAt(createdAt)
                .updatedAt(null)
                .username(row.username())
                .email("-")
                .displayname(row.displayName())
                .role(defaultRole(row.role()))
                .build();
    }

    private String defaultRole(String role) {
        return (role == null || role.isBlank()) ? "USER" : role;
    }

    @Override
    public User save(User entity) {
        throw new UnsupportedOperationException();
    }
}
